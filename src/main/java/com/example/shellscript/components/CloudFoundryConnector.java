package com.example.shellscript.components;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.reactor.DefaultConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.client.ReactorCloudFoundryClient;
import org.cloudfoundry.reactor.tokenprovider.PasswordGrantTokenProvider;

public class CloudFoundryConnector {

    public static CloudFoundryClient connect(String apiHost, String username, String password) {
        // Example: apiHost = "api.yourcf.com"
        DefaultConnectionContext connectionContext = DefaultConnectionContext.builder()
                .apiHost(apiHost)
                .skipSslValidation(true) // optional
                .build();

        TokenProvider tokenProvider = PasswordGrantTokenProvider.builder()
                .username(username)
                .password(password)
                .build();

        return ReactorCloudFoundryClient.builder()
                .connectionContext(connectionContext)
                .tokenProvider(tokenProvider)
                .build();
    }
}
