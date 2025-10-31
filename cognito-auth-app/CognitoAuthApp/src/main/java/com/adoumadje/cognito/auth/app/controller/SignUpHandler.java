package com.adoumadje.cognito.auth.app.controller;

import java.util.HashMap;
import java.util.Map;

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

/**
 * Handler for requests to Lambda function.
 */
public class SignUpHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final CognitoUserService cognitoUserService;
    private final String appClientId;
    private final String appClientSecret;

    public SignUpHandler(CognitoUserService cognitoUserService, String appClientId, String appClientSecret) {
        this.cognitoUserService = cognitoUserService;
        this.appClientId = appClientId;
        this.appClientSecret = appClientSecret;
    }

    public SignUpHandler() {
        this.cognitoUserService = new CognitoUserService(System.getenv("AWS_REGION"));
        this.appClientId = CryptoUtils.decryptEnvVariable("MY_COGNITO_CLIENT_APP_ID");
        this.appClientSecret = CryptoUtils.decryptEnvVariable("MY_COGNITO_CLIENT_APP_SECRET");
    }

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withHeaders(headers);

        String requestBody = input.getBody();
        LambdaLogger logger = context.getLogger();
        logger.log("Original data: " + requestBody);

        try {
            JsonObject userDetails = JsonParser.parseString(requestBody).getAsJsonObject();

            JsonObject createdUser = cognitoUserService.registerUser(userDetails, appClientId, appClientSecret);
            response.withStatusCode(200).withBody(new Gson().toJson(createdUser, JsonObject.class));

        } catch (AwsServiceException ex) {
            logger.log(ex.awsErrorDetails().errorMessage());
            ErrorResponse error = new ErrorResponse(ex.awsErrorDetails().errorMessage());
            String errorJSON = new Gson().toJson(error, ErrorResponse.class);
            response.withStatusCode(ex.statusCode()).withBody(errorJSON);
        } catch (Exception ex) {
            logger.log(ex.getMessage());
            ErrorResponse error = new ErrorResponse(ex.getMessage());
            String errorJSON = new Gson().toJson(error, ErrorResponse.class);
            response.withStatusCode(500).withBody(errorJSON);
        }

        return response;
    }
}
