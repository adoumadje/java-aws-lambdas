package com.adoumadje.authorizer;

import com.auth0.jwk.InvalidPublicKeyException;
import com.auth0.jwk.JwkException;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.JwkProviderBuilder;
import com.auth0.jwt.interfaces.RSAKeyProvider;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

public class AwsCognitoRSAKeyProvider implements RSAKeyProvider {

    private final URL aws_key_set_url;
    private final JwkProvider provider;

    public AwsCognitoRSAKeyProvider(String region, String userPoolID) {
        String url = String.format("http://cognito-idp.%s.amazonaws.com/%s/.well-known/jwks.json",
                region, userPoolID);
        try {
            this.aws_key_set_url = new URL(url);
        } catch (MalformedURLException e) {
            throw new RuntimeException(String.format("Invalid URL %s", url));
        }
        this.provider = new JwkProviderBuilder(aws_key_set_url).build();
    }

    @Override
    public RSAPublicKey getPublicKeyById(String kid) {
        try {
            return (RSAPublicKey) provider.get(kid).getPublicKey();
        } catch (JwkException e) {
            throw new RuntimeException(String.format("Failed to get public key using kid: %s from jwks.json located at: %s",
                    kid, aws_key_set_url));
        }
    }

    @Override
    public RSAPrivateKey getPrivateKey() {
        return null;
    }

    @Override
    public String getPrivateKeyId() {
        return "";
    }
}
