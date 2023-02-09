package com.alphalaneous.FileUtils;

import com.alphalaneous.Main;
import com.alphalaneous.Utils.Defaults;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;


public class GetInternalFiles {

    private final String location;
    private URI uri;
    private Path path;
    public static FileSystem fileSystem;

    public GetInternalFiles(String location){
        this.location = location;
        if(!isInJar()){
            try {
                uri = Objects.requireNonNull(Main.class.getResource("/")).toURI();
            } catch (URISyntaxException ignored) {
            }
            fileSystem = FileSystems.getDefault();
            if(Defaults.isMac())path = fileSystem.getPath(uri.getPath() + location);
            else path = fileSystem.getPath(uri.getPath().substring(3) + location);
        }
    }

    public FileList getFiles(){
        if(isInJar()) {
            try {
                URL jar = Main.class.getProtectionDomain().getCodeSource().getLocation();

                Path jarFile = Paths.get(jar.toString().substring(8).replace("%20", " "));
                FileList list = new FileList();

                FileSystem fs = FileSystems.newFileSystem(jarFile);
                DirectoryStream<Path> directoryStream = Files.newDirectoryStream(fs.getPath(location));
                for (Path p : directoryStream) {
                    list.add(new InternalFile(p));
                }
                return list;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        else {
            try {
                FileList list = new FileList();
                Stream<Path> walk = Files.walk(path, 1);
                for (Iterator<Path> it = walk.iterator(); it.hasNext(); ) {
                    Path p = it.next();
                    list.add(new InternalFile(p));
                }
                return list;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
    public String getPath(){
        return path.toString();
    }

    public static boolean isInJar(){
        String protocol = Objects.requireNonNull(GetInternalFiles.class.getResource("")).getProtocol();
        return protocol.equalsIgnoreCase("jar");
    }
}
