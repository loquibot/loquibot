package com.alphalaneous.Services.YouTube;

import com.alphalaneous.Main;
import com.alphalaneous.Utilities.Logging;
import com.alphalaneous.Utilities.SimpleFileDataStoreFactory;
import com.alphalaneous.Utilities.Utilities;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.DataStore;
import com.google.api.client.util.store.FileDataStoreFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;

public class YouTubeAuth {

    public static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    public static final JsonFactory JSON_FACTORY = new GsonFactory();

    public static AuthorizationCodeInstalledApp app;

    public static Credential authorize(List<String> scopes, String credentialDatastore, boolean refresh) throws IOException {

        if(refresh){
            try {
                if (Files.exists(Path.of(Utilities.saveDirectory + "/" + credentialDatastore))) {
                    Files.delete(Path.of(Utilities.saveDirectory + "/" + credentialDatastore));
                }
            } catch (Exception e) {
                Logging.getLogger().error(e.getLocalizedMessage(), e);
            }
        }

        Reader clientSecretReader = new InputStreamReader(Objects.requireNonNull(YouTubeAuth.class.getResourceAsStream("/client_secrets.json")));
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, clientSecretReader);

        File correctDirectory = new File(Utilities.saveDirectory + "/");

        SimpleFileDataStoreFactory fileDataStoreFactory = null;
        try {
            fileDataStoreFactory = new SimpleFileDataStoreFactory(correctDirectory);
        }
        catch (Exception e){
            Logging.getLogger().error(e.getMessage(), e);
        }

        if(fileDataStoreFactory != null) {

            DataStore<StoredCredential> datastore = fileDataStoreFactory.getDataStore(credentialDatastore);

            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                    HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, scopes).setCredentialDataStore(datastore)
                    .build();

            LocalServerReceiver localReceiver = new LocalServerReceiver.Builder().setPort(8080).build();

            return new AuthorizationCodeInstalledApp(flow, localReceiver).authorize("user");
        }
        return null;
    }
}