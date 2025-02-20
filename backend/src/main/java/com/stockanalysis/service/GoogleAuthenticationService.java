package com.stockanalysis.service;

import io.github.cdimascio.dotenv.Dotenv;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.Arrays;

@Service
public class GoogleAuthenticationService {


    public GoogleAuthenticationService() {
        
    }

    public GoogleIdToken.Payload authenticate(String IDToken) throws IOException {
        Dotenv dotenv = Dotenv.load();
        String client_id = dotenv.get("GOOGLE_CLIENT_ID");
        NetHttpTransport transport = new NetHttpTransport();
        GsonFactory gsonFactory = GsonFactory.getDefaultInstance();
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, gsonFactory)
                .setAudience(Arrays.asList(client_id))
                .build();

        try {
            GoogleIdToken idToken = verifier.verify(IDToken);
            if (idToken != null) {
                return idToken.getPayload();
            } else {
                throw new IOException("Invalid ID token.");
            }
        } catch (Exception e) {
            throw new IOException("Error verifying ID token: " + e.getMessage());
        }
    }
}