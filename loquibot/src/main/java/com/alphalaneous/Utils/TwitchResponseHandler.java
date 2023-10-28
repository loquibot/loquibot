package com.alphalaneous.Utils;

import com.alphalaneous.FileUtils.FileUtils;
import com.alphalaneous.FileUtils.GetInternalFiles;
import com.alphalaneous.FileUtils.InternalFile;
import com.alphalaneous.Main;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class TwitchResponseHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String query = exchange.getRequestURI().getQuery();
        boolean hasAccessToken = false;

        if(query != null) {
            String[] queries = query.split("&");

            for (String q : queries) {
                if (q.startsWith("access_token")) {
                    hasAccessToken = true;
                    break;
                }
            }
        }

        if(hasAccessToken){
            TwitchHTTPServer.setCurrentQuery(query);
        }
        else {
            OutputStream output = exchange.getResponseBody();

            exchange.getResponseHeaders().add("Content-Type", "text/html; charset=UTF-8");

            InputStream inputStream = Main.class.
                    getResourceAsStream("/WebPages/successPage.html");
            if (inputStream != null) {
                InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                BufferedReader in = new BufferedReader(streamReader);

                StringBuilder text = new StringBuilder();

                for (String line; (line = in.readLine()) != null; ) {
                    text.append(line).append("\n");
                }

                exchange.sendResponseHeaders(200, text.length());

                output.write(text.toString().getBytes());
            } else {
                exchange.sendResponseHeaders(404, 0);

                String text404 = "<html><h1>404 Page not found :(</h1></html>";

                output.write(text404.getBytes());
            }
            output.flush();
            output.close();
        }
    }
}
