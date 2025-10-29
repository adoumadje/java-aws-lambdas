package com.adoumadje;

import com.adoumadje.domain.Division;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Handler for requests to Lambda function.
 */
public class DivisionHandler implements RequestHandler<Map<String, String>, Division> {

    public Division handleRequest(final Map<String, String> input, final Context context) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        int dividend = Integer.parseInt(input.get("dividend"));
        int divisor = Integer.parseInt(input.get("divisor"));
        int result = dividend / divisor;

        Division division = new Division();
        division.setDividend(dividend);
        division.setDivisor(divisor);
        division.setResult(result);

        return division;
    }
}
