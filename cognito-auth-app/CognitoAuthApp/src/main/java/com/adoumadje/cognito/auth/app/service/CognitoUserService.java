package com.adoumadje.cognito.auth.app.service;

import com.adoumadje.cognito.auth.app.shared.Constants;
import com.adoumadje.cognito.auth.app.utils.CryptoUtils;
import com.google.gson.JsonObject;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.SignUpRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.SignUpResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CognitoUserService {
    private final CognitoIdentityProviderClient cognitoIdentityProviderClient;

    public CognitoUserService(String awsRegion) {
        this.cognitoIdentityProviderClient = CognitoIdentityProviderClient.builder()
                .region(Region.of(awsRegion))
                .build();
    }

    public CognitoUserService(CognitoIdentityProviderClient cognitoIdentityProviderClient) {
        this.cognitoIdentityProviderClient = cognitoIdentityProviderClient;
    }

    public JsonObject registerUser(JsonObject userDetails, String appClientId, String appClientSecret) {
        String userId = UUID.randomUUID().toString();
        String firstName = userDetails.get("firstName").getAsString();
        String lastName = userDetails.get("lastName").getAsString();
        String email = userDetails.get("email").getAsString();
        String password = userDetails.get("password").getAsString();

        AttributeType userIdAttribute = AttributeType.builder()
                .name("custom:userId").value(userId).build();

        AttributeType fullNameAttribute = AttributeType.builder()
                .name("custom:fullName").value(firstName + " " + lastName).build();

        AttributeType emailAttribute = AttributeType.builder()
                .name("email").value(email).build();

        List<AttributeType> userAttributes = new ArrayList<>();
        userAttributes.add(userIdAttribute);
        userAttributes.add(fullNameAttribute);
        userAttributes.add(emailAttribute);

        String generatedSecretHash = CryptoUtils.calculateSecretHash(appClientId, appClientSecret, email);

        SignUpRequest signUpRequest = SignUpRequest.builder()
                .username(email)
                .password(password)
                .userAttributes(userAttributes)
                .clientId(appClientId)
                .secretHash(generatedSecretHash)
                .build();

        SignUpResponse signUpResponse = cognitoIdentityProviderClient.signUp(signUpRequest);

        JsonObject createdUser = new JsonObject();

        createdUser.addProperty(Constants.IS_SUCCESSFUL, signUpResponse.sdkHttpResponse().isSuccessful());
        createdUser.addProperty(Constants.COGNITO_USER_ID, signUpResponse.userSub());
        createdUser.addProperty(Constants.STATUS_CODE, signUpResponse.sdkHttpResponse().statusCode());
        createdUser.addProperty(Constants.IS_CONFIRM, signUpResponse.userConfirmed());

        return createdUser;
    }
}
