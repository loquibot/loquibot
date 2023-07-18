package com.alphalaneous.FileUtils;

import java.util.ArrayList;

public class FileList extends ArrayList<InternalFile> {

    public InternalFile getFile(String name){
        for(InternalFile file : this) {

            String[] splitForward = file.getName().split("/");
            String[] splitBackward = file.getName().split("\\\\");

            String nameF = splitForward[splitForward.length-1];
            String nameB = splitBackward[splitBackward.length-1];

            if(nameF.equalsIgnoreCase(name) || nameB.equalsIgnoreCase(name)) return file;
        }
        return null;
    }
}
