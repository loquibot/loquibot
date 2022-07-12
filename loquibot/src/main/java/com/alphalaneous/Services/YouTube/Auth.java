package com.alphalaneous.Services.YouTube;

import com.alphalaneous.Defaults;
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

public class Auth {

    public static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    public static final JsonFactory JSON_FACTORY = new GsonFactory();

    public static Credential authorize(List<String> scopes, String credentialDatastore, boolean refresh) throws IOException {

        String credentials = credentialDatastore;
        if(refresh) credentials = credentialDatastore + "_temp";


        Reader clientSecretReader = new InputStreamReader(Objects.requireNonNull(Auth.class.getResourceAsStream("/client_secrets.json")));
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, clientSecretReader);

        File correctDirectory = new File(Defaults.saveDirectory + "\\loquibot");

        FileDataStoreFactory fileDataStoreFactory = new FileDataStoreFactory(correctDirectory);

        DataStore<StoredCredential> datastore = fileDataStoreFactory.getDataStore(credentials);

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, scopes).setCredentialDataStore(datastore)
                .build();

        LocalServerReceiver localReceiver = new LocalServerReceiver.Builder().setPort(8080).build();

        Credential credential = new AuthorizationCodeInstalledApp(flow, localReceiver).authorize("user");

        if(refresh) {
            Path originalPath = Paths.get(Defaults.saveDirectory + "\\loquibot\\" + credentialDatastore);
            Path tempPath = Paths.get(Defaults.saveDirectory + "\\loquibot\\" + credentialDatastore + "_temp");
            Files.move(tempPath, originalPath, StandardCopyOption.REPLACE_EXISTING);
        }
        return credential;
    }

}