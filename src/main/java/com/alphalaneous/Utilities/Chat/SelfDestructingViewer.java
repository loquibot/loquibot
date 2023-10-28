package com.alphalaneous.Utilities.Chat;

import com.alphalaneous.Utilities.Utilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SelfDestructingViewer {

    private static final List<SelfDestructingViewer> selfDestructingViewers = Collections.synchronizedList(new ArrayList<>());
    private final String viewer;
    public SelfDestructingViewer(String viewer){

        if(containsViewer(viewer)) {
            removeByViewer(viewer);
        }

        this.viewer = viewer;
        new Thread(() -> {
            selfDestructingViewers.add(this);
            Utilities.sleep(60000*3);
            selfDestructingViewers.remove(this);
        }).start();
    }

    public String getViewer(){
        return viewer;
    }

    public static void removeByViewer(String viewer){
        for(SelfDestructingViewer viewer1 : selfDestructingViewers){
            if(viewer1 != null) {
                if (viewer1.getViewer().equalsIgnoreCase(viewer)) {
                    selfDestructingViewers.remove(viewer1);
                    break;
                }
            }
        }
    }

    public static boolean containsViewer(String viewer){
        for(SelfDestructingViewer viewer1 : selfDestructingViewers){
            if(viewer1 != null) {
                if (viewer1.getViewer().trim().equalsIgnoreCase(viewer.trim())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static int getSize(){
        return selfDestructingViewers.size();
    }

}
