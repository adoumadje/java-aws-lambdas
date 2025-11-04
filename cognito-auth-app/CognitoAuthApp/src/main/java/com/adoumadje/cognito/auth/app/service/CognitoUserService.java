package com.adoumadje.cognito.auth.app.service;

import com.adoumadje.cognito.auth.app.constants.AuthParams;
import com.adoumadje.cognito.auth.app.constants.Constants;
import com.adoumadje.cognito.auth.app.utils.CryptoUtils;
import com.google.gson.JsonObject;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

import java.util.*;

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
                .name("custom:fullName").value(firstName).build();

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

    public JsonObject confirmUser(JsonObject confirmationDetails, String appClientID, String appClientSecret) {
        String email = confirmationDetails.get("email").getAsString();
        String code = confirmationDetails.get("code").getAsString();

        String generatedSecretHash = CryptoUtils.calculateSecretHash(appClientID, appClientSecret, email);

        ConfirmSignUpRequest confirmSignUpRequest = ConfirmSignUpRequest.builder()
                .clientId(appClientID)
                .secretHash(generatedSecretHash)
                .username(email)
                .confirmationCode(code)
                .build();

        ConfirmSignUpResponse confirmSignUpResponse = cognitoIdentityProviderClient.confirmSignUp(confirmSignUpRequest);

        JsonObject confirmationResult = new JsonObject();

        confirmationResult.addProperty(Constants.IS_SUCCESSFUL, confirmSignUpResponse.sdkHttpResponse().isSuccessful());
        confirmationResult.addProperty(Constants.STATUS_CODE, confirmSignUpResponse.sdkHttpResponse().statusCode());

        return confirmationResult;

    }

    public JsonObject loginUser(JsonObject loginDetails, String appClientID, String appClientSecret) {
        String email = loginDetails.get("email").getAsString();
        String password = loginDetails.get("password").getAsString();

        String generatedSecretHash = CryptoUtils.calculateSecretHash(appClientID, appClientSecret, email);

        Map<String, String> authParams = new HashMap<>();
        authParams.put(AuthParams.USERNAME, email);
        authParams.put(AuthParams.PASSWORD, password);
        authParams.put(AuthParams.SECRET_HASH, generatedSecretHash);

        InitiateAuthRequest initiateAuthRequest = InitiateAuthRequest.builder()
                .clientId(appClientID)
                .authFlow(AuthFlowType.USER_PASSWORD_AUTH)
                .authParameters(authParams).build();

        InitiateAuthResponse initiateAuthResponse = cognitoIdentityProviderClient.initiateAuth(initiateAuthRequest);
        AuthenticationResultType authenticationResultType = initiateAuthResponse.authenticationResult();

        JsonObject loginResult = new JsonObject();

        loginResult.addProperty(Constants.IS_SUCCESSFUL, initiateAuthResponse.sdkHttpResponse().isSuccessful());
        loginResult.addProperty(Constants.STATUS_CODE, initiateAuthResponse.sdkHttpResponse().statusCode());

        loginResult.addProperty(AuthParams.ID_TOKEN, authenticationResultType.idToken());
        loginResult.addProperty(AuthParams.ACCESS_TOKEN, authenticationResultType.accessToken());
        loginResult.addProperty(AuthParams.REFRESH_TOKEN, authenticationResultType.refreshToken());

        return loginResult;
    }

    public JsonObject addUserToGroup(JsonObject groupDetails, String userPoolID) {
        String username = groupDetails.get("username").getAsString();
        String groupName = groupDetails.get("groupName").getAsString();

        AdminAddUserToGroupRequest adminAddUserToGroupRequest = AdminAddUserToGroupRequest.builder()
                .groupName(groupName)
                .username(username)
                .userPoolId(userPoolID).build();

        AdminAddUserToGroupResponse adminAddUserToGroupResponse = cognitoIdentityProviderClient
                .adminAddUserToGroup(adminAddUserToGroupRequest);

        JsonObject addUserResult = new JsonObject();

        addUserResult.addProperty(Constants.IS_SUCCESSFUL, adminAddUserToGroupResponse.sdkHttpResponse().isSuccessful());
        addUserResult.addProperty(Constants.STATUS_CODE, adminAddUserToGroupResponse.sdkHttpResponse().statusCode());

        return addUserResult;
    }
}
