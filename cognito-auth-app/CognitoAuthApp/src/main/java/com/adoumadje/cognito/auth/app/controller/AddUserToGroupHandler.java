package com.adoumadje.cognito.auth.app.controller;

import com.adoumadje.cognito.auth.app.service.CognitoUserService;
import com.adoumadje.cognito.auth.app.shared.ErrorResponse;
import com.adoumadje.cognito.auth.app.utils.CryptoUtils;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import software.amazon.awssdk.awscore.exception.AwsServiceException;

import java.util.HashMap;
import java.util.Map;

public class AddUserToGroupHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private final CognitoUserService cognitoUserService;
    private final String userPoolID;

    public AddUserToGroupHandler() {
        this.cognitoUserService = new CognitoUserService(System.getenv("AWS_REGION"));
        this.userPoolID = CryptoUtils.decryptEnvVariable("MY_COGNITO_USER_POOL_ID");
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        response.withHeaders(headers);
        Gson gson = new Gson();
        LambdaLogger logger = context.getLogger();

        try {
            String requestBody = input.getBody();
            JsonObject groupDetails = JsonParser.parseString(requestBody).getAsJsonObject();
            JsonObject addUserResult = cognitoUserService.addUserToGroup(groupDetails, userPoolID);
            response.withStatusCode(200).withBody(gson.toJson(addUserResult));
        } catch (AwsServiceException ex) {
            logger.log(ex.awsErrorDetails().errorMessage());
            ErrorResponse error = new ErrorResponse(ex.awsErrorDetails().errorMessage());
            response.withStatusCode(ex.statusCode()).withBody(gson.toJson(error));
        } catch (Exception ex) {
            logger.log(ex.getMessage());
            ErrorResponse error = new ErrorResponse(ex.getMessage());
            response.withStatusCode(500).withBody(gson.toJson(error));
        }

        return response;
    }
}
