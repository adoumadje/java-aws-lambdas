package com.adoumadje;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.adoumadje.domain.Division;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;

/**
 * Handler for requests to Lambda function.
 */
public class DivisionHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        Map<String, String> params = input.getQueryStringParameters();

        int dividend = Integer.parseInt(params.get("dividend"));
        int divisor = Integer.parseInt(params.get("divisor"));

        Division division = new Division();
        division.setDividend(dividend);
        division.setDivisor(divisor);
        division.setResult(dividend / divisor);

        Gson gson = new Gson();

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        response.withHeaders(headers).withStatusCode(200).withBody(gson.toJson(division, Division.class));

        return response;
    }
}
