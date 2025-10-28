package com.adoumadje;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import com.adoumadje.domain.RequestUser;
import com.adoumadje.domain.ResponseUser;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

/**
 * Handler for requests to Lambda function.
 */
public class PostUserHandler implements RequestHandler<RequestUser, ResponseUser> {

    public ResponseUser handleRequest(final RequestUser input, final Context context) {
        ResponseUser response = new ResponseUser();
        response.setId(UUID.randomUUID());
        response.setFirstName(input.getFirstName());
        response.setLastName(input.getLastName());
        response.setEmail(input.getEmail());
        return response;
    }
}
