package com.alphalaneous.Utilities;

import com.google.api.client.util.IOUtils;
import com.google.api.client.util.Maps;
import com.google.api.client.util.store.AbstractDataStoreFactory;
import com.google.api.client.util.store.AbstractMemoryDataStore;
import com.google.api.client.util.store.DataStore;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SimpleFileDataStoreFactory extends AbstractDataStoreFactory {
    private final File dataDirectory;

    public SimpleFileDataStoreFactory(File dataDirectory) throws IOException {
        dataDirectory = dataDirectory.getCanonicalFile();
        this.dataDirectory = dataDirectory;
    }

    protected <V extends Serializable> DataStore<V> createDataStore(String id) throws IOException {
        return new FileDataStore<>(this, this.dataDirectory, id);
    }

    static class FileDataStore<V extends Serializable> extends AbstractMemoryDataStore<V> {
        private final Path dataFile;

        FileDataStore(SimpleFileDataStoreFactory dataStore, File dataDirectory, String id) throws IOException {
            super(dataStore, id);

            this.dataFile = Paths.get(dataDirectory.toString(), id);

            if(!Files.exists(dataFile)){
                Files.createFile(dataFile);
                this.keyValueMap = Maps.newHashMap();
                this.save();
            }
            else{
                this.keyValueMap = IOUtils.deserialize(new FileInputStream(this.dataFile.toFile()));
            }
        }

        public void save() throws IOException {
            IOUtils.serialize(this.keyValueMap, new FileOutputStream(this.dataFile.toFile()));
        }

        public com.google.api.client.util.store.FileDataStoreFactory getDataStoreFactory() {
            return (com.google.api.client.util.store.FileDataStoreFactory)super.getDataStoreFactory();
        }
    }
}
