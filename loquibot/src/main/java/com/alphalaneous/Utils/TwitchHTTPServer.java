package com.alphalaneous.Utils;

import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;

public class TwitchHTTPServer {


    private static HttpServer httpServer;
    private static String currentQuery = null;

    public static void start() {

        try {
            InetSocketAddress address = new InetSocketAddress(23522);

            httpServer = HttpServer.create(address, 0);

            httpServer.setExecutor(null);
            httpServer.createContext("/", new TwitchResponseHandler());
            httpServer.start();


        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static void stop(){
        httpServer.stop(0);
    }

    private static String accessToken = null;

    public static void awaitAccessToken(){

        start();


        while (currentQuery == null){
            Utilities.sleep(10);
        }

        System.out.println("Got Here");

        String[] params = currentQuery.split("&");

        for(String param : params){
            if(param.startsWith("access_token")) accessToken = param.split("=")[1];
        }
        currentQuery = null;

        stop();
    }

    public static String getAccessToken(){
        return accessToken;
    }

    public static void setCurrentQuery(String currentQuery){
        TwitchHTTPServer.currentQuery = currentQuery;
    }
}
