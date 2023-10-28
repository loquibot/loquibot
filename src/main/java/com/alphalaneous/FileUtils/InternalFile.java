package com.alphalaneous.FileUtils;

import com.alphalaneous.Main;

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class InternalFile {

    private final Path path;

    public InternalFile(Path path){
        this.path = path;
    }

    public Path getPath(){
        return path;
    }

    public String getName(){
        String[] file;
        if (GetInternalFiles.isInJar()) file = path.toString().split("/");
        else file = path.toString().split("\\\\");
        return file[file.length - 1];
    }

    public String getString(){
        try {
            if (GetInternalFiles.isInJar()) {
                InputStream is = Main.class
                        .getClassLoader().getResourceAsStream(path.toString());
                assert is != null;
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                StringBuilder string = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    string.append(line).append("\n");
                }
                is.close();
                isr.close();
                br.close();

                return string.toString();
            } else return Files.readString(path);

        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public Font getFont(){

        try{
            if(GetInternalFiles.isInJar()){
                return Font.createFont(Font.TRUETYPE_FONT,
                        Objects.requireNonNull(Main.class.getClassLoader().getResourceAsStream(path.toString())));
            }
            else{
                return Font.createFont(Font.TRUETYPE_FONT, path.toFile());
            }
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

}
