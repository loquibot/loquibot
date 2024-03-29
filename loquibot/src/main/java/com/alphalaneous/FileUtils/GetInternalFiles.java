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
    private String uri;
    private Path path;
    public static FileSystem fileSystem;

    public GetInternalFiles(String location){
        this.location = location;
        if(!isInJar()){
            uri = Objects.requireNonNull(Main.class.getResource("/")).getPath();
            fileSystem = FileSystems.getDefault();
            if(Defaults.isMac()){
                path = fileSystem.getPath(uri + location);
            }
            else {
                path = fileSystem.getPath(uri.substring(3) + location);
            }
        }
        else{
            path = Path.of(location);
        }
    }

    public FileList getFiles(){
        if(isInJar()) {
            try {
                URL jar = Main.class.getProtectionDomain().getCodeSource().getLocation();
                Path jarFile;

                if(Defaults.isMac()){
                    jarFile = Paths.get(jar.toString().substring(5).replace("%20", " "));
                }
                else{
                    jarFile = Paths.get(jar.toString().substring(8).replace("%20", " "));
                }

                FileList list = new FileList();

                FileSystem fs = FileSystems.newFileSystem(jarFile);
                DirectoryStream<Path> directoryStream = Files.newDirectoryStream(fs.getPath(location));
                for (Path p : directoryStream) {
                    list.add(new InternalFile(p));
                }
                return list;
            } catch (Exception e) {
                Main.logger.error(e.getLocalizedMessage(), e);
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
                Main.logger.error(e.getLocalizedMessage(), e);
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
