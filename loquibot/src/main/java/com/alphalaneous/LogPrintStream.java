package com.alphalaneous;

import java.io.*;

public class LogPrintStream extends PrintStream {

    private final PrintStream mainStream;
    public LogPrintStream(OutputStream out, PrintStream mainStream) {
        super(out);
        this.mainStream = mainStream;
    }
    @Override
    public void write(byte[] buf, int off, int len) {
        super.write(buf, off, len);
        mainStream.write(buf, off, len);
    }
}
