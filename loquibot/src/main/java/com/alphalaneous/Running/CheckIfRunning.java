package com.alphalaneous.Running;

import com.alphalaneous.Main;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class CheckIfRunning extends WebSocketClient {



    public CheckIfRunning(URI uri) {
        super(uri);

    }

    public void check(){
        sendMessage();
    }

    private void sendMessage() {
        send("opened");

    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        check();
    }

    @Override
    public void onMessage(String s) {
        if(s.equalsIgnoreCase("yes")){
            System.exit(0);
        }
    }

    @Override
    public void onClose(int i, String s, boolean b) {

    }

    @Override
    public void onError(Exception e) {
        Main.logger.info("New loquibot instance");
    }
}
