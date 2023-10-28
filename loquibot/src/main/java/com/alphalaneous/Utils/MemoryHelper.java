package com.alphalaneous.Utils;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.*;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.W32APIOptions;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static com.sun.jna.platform.win32.WinNT.PROCESS_QUERY_INFORMATION;
import static com.sun.jna.platform.win32.WinNT.PROCESS_VM_READ;

public class MemoryHelper {

    private static final Kernel32 kernel32 = Native.loadLibrary("kernel32", Kernel32.class, W32APIOptions.DEFAULT_OPTIONS);
    private static final Kernel32b kernel32b = Native.loadLibrary("kernel32", Kernel32b.class, W32APIOptions.ASCII_OPTIONS);

    private static final User32 user32 = Native.loadLibrary("user32", User32.class, W32APIOptions.DEFAULT_OPTIONS);
    private static final long base = 0x3222d0;
    private static long gameBase;
    private static WinNT.HANDLE hProcess;

    private static boolean initialized = false;




    public static void init(){

        if(!System.getProperty("os.name").toLowerCase().startsWith("windows")){
            throw new OSNotSupportedException(System.getProperty("os.name") + " is not supported by Dash4j");
        }

        initialized = true;
        checkPID(false);

        hProcess = openProcess(PID);

        new Thread(() -> checkPID(true)).start();
        new Thread(() -> {
            while(true) {
                hProcess = openProcess(PID);
                Utilities.sleep(100);
            }
        }).start();
    }

    public static boolean isInFocus(){
        WinDef.HWND windowHandle = user32.GetForegroundWindow();
        IntByReference pid= new IntByReference();
        user32.GetWindowThreadProcessId(windowHandle, pid);
        WinNT.HANDLE processHandle=kernel32.OpenProcess(PROCESS_VM_READ | PROCESS_QUERY_INFORMATION, true, pid.getValue());

        char[] filename = new char[512];
        Psapi.INSTANCE.GetModuleFileNameExW(processHandle, null, filename, filename.length);
        String name=new String(filename);
        name = name.replace("\0", "");

        return name.endsWith("GeometryDash.exe");
    }

    public static Memory read(int[] offsets, int bytesToRead) {
        if(!initialized){
            throw new GDNotInitializedException("Memory not initialized, use Memory.init() to initialize.");
        }

        long addr = findDynAddress(hProcess, offsets, gameBase);
        return readMemory(hProcess, addr, bytesToRead);
    }
    public static String readString(int[] offsets, int bytesToRead){

        if(!initialized){
            throw new GDNotInitializedException("Memory not initialized, use Memory.init() to initialize.");
        }

        long addr = findDynAddress(hProcess, offsets, gameBase);

        int length = readMemory(hProcess, addr + 0x10, bytesToRead).getInt(0);

        String string;

        try {
            if (length >= 16) {
                string = readMemory(hProcess, readMemory(hProcess, addr, bytesToRead).getInt(0), length).getString(0);
            } else {
                string = readMemory(hProcess, addr, bytesToRead).getString(0).substring(0, length);
            }
        }
        catch (StringIndexOutOfBoundsException e){
            return null;
        }
        if(string.length() < length) return null;
        else string = string.substring(0, length);

        if(string.equalsIgnoreCase("")) return null;

        return string;
    }


    private static Memory readMemory(WinNT.HANDLE process, long address, int bytesToRead) {
        IntByReference read = new IntByReference(0);
        Memory output = new Memory(bytesToRead);
        kernel32.ReadProcessMemory(process, Pointer.createConstant(address), output, bytesToRead, read);
        return output;
    }

    public static void writeBytes(int[] offsets, byte[] value){
        writeMemory(hProcess, offsets, value);
    }

    public static void writeInt(int[] offsets, int value){
        writeMemory(hProcess, offsets, ByteBuffer.allocate(4).order(ByteOrder.nativeOrder()).putInt(value).array());
    }

    public static void writeFloat(int[] offsets, float value){
        writeMemory(hProcess, offsets, ByteBuffer.allocate(4).order(ByteOrder.nativeOrder()).putFloat(value).array());
    }

    public static void writeString(int[] offsets, String value){
        writeMemory(hProcess, offsets, value.getBytes());
    }

    //write bytes to address

    public static void writeToAddress(long address, byte[] data){

        address = GetModuleBaseAddress(PID) + address;

        int size = data.length;
        Memory toWrite = new  Memory(size);

        for(int i = 0; i < size; i++) {
            toWrite.setByte(i, data[i]);
        }
        kernel32.WriteProcessMemory(hProcess, Pointer.createConstant(address), toWrite, size, null);
    }


    private static void writeMemory(WinNT.HANDLE process, int[] offsets, byte[] data) {

        if(!initialized){
            throw new GDNotInitializedException("Memory not initialized, use Memory.init() to initialize.");
        }

        long addr = findDynAddress(hProcess, offsets, gameBase);

        int size = data.length;
        Memory toWrite = new  Memory(size);

        for(int i = 0; i < size; i++) {
            toWrite.setByte(i, data[i]);
        }
        kernel32.WriteProcessMemory(process, Pointer.createConstant(addr), toWrite, size, null);
    }

    private static long findDynAddress(WinNT.HANDLE process, int[] offsets, long baseAddress) {
        Pointer pointer = new Pointer(baseAddress);
        int size = 4;
        Memory pTemp = new Memory(size);
        long pointerAddress = 0;

        for(int i = 0; i < offsets.length; i++) {
            if(i == 0) {
                kernel32.ReadProcessMemory(process, pointer, pTemp, size, null);
            }
            pointerAddress = pTemp.getInt(0)+offsets[i];
            if(i != offsets.length-1) kernel32.ReadProcessMemory(process, new Pointer(pointerAddress), pTemp, size, null);
        }
        return pointerAddress;
    }

    private static int PID;

    public static String getExePath(){
        Tlhelp32.PROCESSENTRY32.ByReference processEntry = new Tlhelp32.PROCESSENTRY32.ByReference();
        WinNT.HANDLE processSnapshot =
                kernel32.CreateToolhelp32Snapshot(Tlhelp32.TH32CS_SNAPPROCESS, new WinDef.DWORD(0));
        try {
            while (kernel32.Process32Next(processSnapshot, processEntry)) {

                if (Native.toString(processEntry.szExeFile).equalsIgnoreCase("GeometryDash.exe")) {
                    WinNT.HANDLE moduleSnapshot = kernel32.CreateToolhelp32Snapshot(Tlhelp32.TH32CS_SNAPMODULE, processEntry.th32ProcessID);
                    try {
                        Tlhelp32.MODULEENTRY32W.ByReference me = new Tlhelp32.MODULEENTRY32W.ByReference();
                        kernel32.Module32FirstW(moduleSnapshot, me);
                        return me.szExePath();
                    }
                    finally {
                        kernel32.CloseHandle(moduleSnapshot);
                    }
                }
            }
        }
        finally {
            kernel32.CloseHandle(processSnapshot);
        }
        return "";
    }

    private static void checkPID(boolean doLoop){
        do {
            IntByReference pid = new IntByReference(0);

            user32.GetWindowThreadProcessId(user32.FindWindow(null, "Geometry Dash"), pid);

            PID = pid.getValue();

            gameBase = GetModuleBaseAddress(PID) + base;

            isGDOpen = PID != 0;
            Utilities.sleep(1000);
        }
        while (doLoop);
    }
    private static WinNT.HANDLE openProcess(int pid) {
        return kernel32.OpenProcess(56, true, pid);
    }

    private static boolean isGDOpen = false;

    public static boolean isGDOpen(){
        return isGDOpen;
    }

    private static long GetModuleBaseAddress(int procID){
        WinDef.DWORD pid = new WinDef.DWORD(procID);
        WinNT.HANDLE hSnap = kernel32.CreateToolhelp32Snapshot(Tlhelp32.TH32CS_SNAPMODULE, pid);
        Tlhelp32.MODULEENTRY32W module = new Tlhelp32.MODULEENTRY32W();

        while(Kernel32.INSTANCE.Module32NextW(hSnap, module)) {

            String s = Native.toString(module.szModule);

            if(s.equals("GeometryDash.exe")){
                Pointer x = module.modBaseAddr;
                return Pointer.nativeValue(x);
            }
        }
        kernel32.CloseHandle(hSnap);

        return -1;
    }

    public static boolean injectDLL(String dllName) {

        BaseTSD.DWORD_PTR loadLibraryAddress = kernel32b.GetProcAddress(kernel32b.GetModuleHandle("KERNEL32"), "LoadLibraryA");
        if(loadLibraryAddress.intValue() == 0) {
            System.out.println("Could not find LoadLibrary! Error: " + kernel32b.GetLastError());
            return false;
        }

        WinDef.LPVOID dllNameAddress = kernel32b.VirtualAllocEx(hProcess, null, (dllName.length() + 1), new BaseTSD.DWORD_PTR(0x3000), new BaseTSD.DWORD_PTR(0x4));
        if(dllNameAddress == null) {
            System.out.println("dllNameAddress was NULL! Error: " + kernel32b.GetLastError());
            return false;
        }

        Pointer m = new Memory(dllName.length() + 1);
        m.setString(0, dllName);

        boolean wpmSuccess = kernel32b.WriteProcessMemory(hProcess, dllNameAddress, m, dllName.length(), null).booleanValue();
        if(!wpmSuccess) {
            System.out.println("WriteProcessMemory failed! Error: " + kernel32b.GetLastError());
            return false;
        }

        BaseTSD.DWORD_PTR threadHandle = kernel32b.CreateRemoteThread(hProcess, 0, 0, loadLibraryAddress, dllNameAddress, 0, 0);
        if(threadHandle.intValue() == 0) {
            System.out.println("threadHandle was invalid! Error: " + kernel32b.GetLastError());
            return false;
        }

        kernel32.CloseHandle(hProcess);
        return true;
    }

    static {
        MemoryHelper.init();
    }
}
