package com.adoumadje;

import com.adoumadje.domain.Division;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.util.Map;

/**
 * Handler for requests to Lambda function.
 */
public class DivisionHandler implements RequestHandler<Map<String, String>, Division> {

    public Division handleRequest(final Map<String, String> input, final Context context) {
        return null;
    }
}
