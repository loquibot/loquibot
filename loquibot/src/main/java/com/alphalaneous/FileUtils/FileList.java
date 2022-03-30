package com.alphalaneous.FileUtils;

import java.util.ArrayList;

public class FileList extends ArrayList<InternalFile> {

    public InternalFile getFile(String name){
        for(InternalFile file : this){
            if(file.getName().equalsIgnoreCase(name)){
                return file;
            }
        }
        return null;
    }
}
