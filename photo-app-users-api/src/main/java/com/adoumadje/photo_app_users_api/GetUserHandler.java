package com.adoumadje.photo_app_users_api;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.JsonObject;

import java.util.Map;

public class GetUserHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
        Map<String, String> pathParams = requestEvent.getPathParameters();
        String userId = pathParams.get("userId");

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", userId);
        jsonObject.addProperty("firstName", "Nuno");
        jsonObject.addProperty("lastName", "Mendes");

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        response.setStatusCode(200);
        response.setBody(jsonObject.toString());
        return response;
    }
}
