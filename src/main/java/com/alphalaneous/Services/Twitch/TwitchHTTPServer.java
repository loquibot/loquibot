package com.alphalaneous.Services.Twitch;

import com.alphalaneous.Utilities.Logging;
import com.alphalaneous.Utilities.Utilities;
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


        } catch (Exception e) {
            Logging.getLogger().error(e.getMessage(), e);
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
