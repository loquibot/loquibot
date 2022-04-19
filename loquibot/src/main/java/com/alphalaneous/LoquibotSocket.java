package com.alphalaneous;

import com.alphalaneous.Windows.Window;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

public class LoquibotSocket extends WebSocketServer {

    private static final int portNumber = 18562;

    public LoquibotSocket() {
        super(new InetSocketAddress(portNumber));
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {

    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {

    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {
        System.out.println(s);
        if(s.equalsIgnoreCase("opened")){
            sendMessage("yes");
            Window.setVisible(true);
        }
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {

    }

    @Override
    public void onStart() {

    }

    public void sendMessage(String message){
        new Thread(() -> broadcast(message)).start();
    }
}
