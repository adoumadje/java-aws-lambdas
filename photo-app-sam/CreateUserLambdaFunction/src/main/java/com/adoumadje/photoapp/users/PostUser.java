package com.adoumadje.photoapp.users;

import com.adoumadje.photoapp.users.domain.User;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;

import java.util.UUID;

/**
 * Handler for requests to Lambda function.
 */
public class PostUser implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        String postDataJSON = input.getBody();

        Gson gson = new Gson();
        User user = gson.fromJson(postDataJSON, User.class);
        user.setId(UUID.randomUUID());

        // Business Logic

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        response.withStatusCode(200).withBody(gson.toJson(user, User.class));

        return response;
    }
}
