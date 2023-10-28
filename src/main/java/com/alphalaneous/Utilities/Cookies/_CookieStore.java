package com.alphalaneous.Utilities.Cookies;

import com.alphalaneous.Utilities.Logging;
import com.alphalaneous.Utilities.Utilities;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InaccessibleObjectException;
import java.lang.reflect.Type;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class _CookieStore implements CookieStore {

    private List<CookieData> cookies = new ArrayList<>();
    private final String fileName = Utilities.saveDirectory + "cookies.json";
    @Override
    public void add(URI uri, HttpCookie cookie) {

        cookies.add(new CookieData(uri,cookie));
        saveCookieStoreToFile();
    }

    @Override
    public List<HttpCookie> get(URI uri) {

        List<HttpCookie> uriCookies = new ArrayList<>();
        CookieData[] tempCookies = cookies.toArray(CookieData[]::new);
        for (CookieData c: tempCookies) {
            if(c.uri.toString().contains(uri.getRawAuthority())){
                uriCookies.add(c.toCookie());
            }
        }
        return uriCookies;
    }

    @Override
    public List<HttpCookie> getCookies() {
        List<HttpCookie> httpCookies = new ArrayList<>();
        CookieData[] tempCookies = cookies.toArray(CookieData[]::new);
        for (CookieData c : tempCookies) {
            httpCookies.add(c.toCookie());
        }

        return httpCookies;
    }

    @Override
    public List<URI> getURIs() {
        return null;
    }

    @Override
    public boolean remove(URI uri, HttpCookie cookie) {
        boolean removed = false;

        if(cookie == null) throw new NullPointerException();


        for(CookieData cookieData : cookies){
            if(cookieData.uri.equals(uri) && cookieData.toCookie().equals(cookie)){
                cookies.remove(cookieData);
                removed = true;
            }
        }

        saveCookieStoreToFile();
        return removed;
    }

    @Override
    public boolean removeAll() {

        if(cookies.isEmpty()) return false;
        else cookies.clear();

        return true;
    }

    public void restoreCookieStoreFromFile(){
        try {
            File file = new File(fileName);
            if (file.exists()) {
                String json = new String(Files.readAllBytes(Paths.get(fileName)));
                Gson gson = new GsonBuilder().create();
                Type type = new TypeToken<List<CookieData>>() {
                }.getType();
                cookies = gson.fromJson(json, type);

            }
        } catch(FileNotFoundException e){
            Logging.getLogger().info("File not found");
        } catch(IOException e){
            Logging.getLogger().info("Can't create file stream");
        }
    }

    private void saveCookieStoreToFile(){
        try {
            Gson gson = new GsonBuilder().create();
            String jsonCookie = gson.toJson(cookies);
            Files.writeString(Path.of(fileName),jsonCookie);

        } catch (FileNotFoundException e) {
            // file not found
            Logging.getLogger().info("Can't Save File");
            e.printStackTrace();
        } catch (IOException e) {
            Logging.getLogger().info("Can't Save File OUTSTREAM");
            // can't create output stream
            e.printStackTrace();
        }
        catch (InaccessibleObjectException e){
            Logging.getLogger().info("Can't Access object");
            e.printStackTrace();
        }
    }
}
