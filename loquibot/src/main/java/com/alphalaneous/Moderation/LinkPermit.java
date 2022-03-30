package com.alphalaneous.Moderation;

import java.util.ArrayList;

public class LinkPermit {

    private static final ArrayList<String> permits = new ArrayList<>();

    public static void giveLinkPermit(String username){
        new Thread(() -> {
            permits.add(username);
            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            permits.remove(username);
        }).start();
    }

    public static boolean checkPermit(String username){
        return permits.contains(username);
    }
}
