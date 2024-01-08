package com.alphalaneous.Services.YouTube;

import com.alphalaneous.Utilities.Logging;
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

        String credentials = credentialDatastore;
        Path credentialDir = Path.of(Utilities.saveDirectory + credentialDatastore);
        if (refresh) {
            credentials = credentialDatastore + "_temp";

            try {
                if (Files.exists(credentialDir)) {
                    Files.delete(credentialDir);
                }
            } catch (Exception e) {
                Logging.getLogger().error(e.getMessage(), e);

            }
        }

        Reader clientSecretReader = new InputStreamReader(Objects.requireNonNull(YouTubeAuth.class.getResourceAsStream("/client_secrets.json")));
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, clientSecretReader);

        File correctDirectory = new File(Utilities.saveDirectory);

        FileDataStoreFactory fileDataStoreFactory = null;
        try {
            fileDataStoreFactory = new FileDataStoreFactory(correctDirectory);
        }
        catch (Exception e){
            Logging.getLogger().error(e.getMessage(), e);
        }

        if(fileDataStoreFactory == null) return null;

        DataStore<StoredCredential> datastore = fileDataStoreFactory.getDataStore(credentials);

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, scopes).setCredentialDataStore(datastore)
                .build();

        LocalServerReceiver localReceiver = new LocalServerReceiver.Builder().setPort(8080).build();

        app = new AuthorizationCodeInstalledApp(flow, localReceiver);

        Credential credential = app.authorize("user");

        if(refresh) {
            Path tempPath = Paths.get(Utilities.saveDirectory + credentialDatastore + "_temp");
            Files.move(tempPath, credentialDir, StandardCopyOption.REPLACE_EXISTING);
        }

        return credential;
    }

}