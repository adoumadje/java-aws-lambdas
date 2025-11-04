package com.adoumadje.cognito.auth.app.controller;

import com.adoumadje.cognito.auth.app.service.CognitoUserService;
import com.adoumadje.cognito.auth.app.shared.ErrorResponse;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import software.amazon.awssdk.awscore.exception.AwsServiceException;

import java.util.HashMap;
import java.util.Map;

public class GetUserHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private final CognitoUserService cognitoUserService;

    public GetUserHandler() {
        this.cognitoUserService = new CognitoUserService(System.getenv("AWS_REGION"));
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        LambdaLogger logger = context.getLogger();
        Gson gson = new Gson();

        try {
            String accessToken = input.getHeaders().get("Access-Token");
            JsonObject requestResult = cognitoUserService.getUser(accessToken);
            response.withStatusCode(200).withBody(gson.toJson(requestResult));
        } catch (AwsServiceException ex) {
            logger.log(ex.awsErrorDetails().errorMessage());
            ErrorResponse error = new ErrorResponse(ex.awsErrorDetails().errorMessage());
            response.withStatusCode(ex.statusCode()).withBody(gson.toJson(error));
        } catch (Exception ex) {

        }

        return response;
    }
}
