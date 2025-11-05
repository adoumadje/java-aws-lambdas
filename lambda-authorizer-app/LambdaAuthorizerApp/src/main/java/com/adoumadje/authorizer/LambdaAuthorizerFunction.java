package com.adoumadje.authorizer;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.auth0.jwt.interfaces.DecodedJWT;

public class LambdaAuthorizerFunction implements RequestHandler<APIGatewayProxyRequestEvent, AuthorizerOuput> {
    @Override
    public AuthorizerOuput handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("Authenticating...");

        String username = input.getPathParameters().get("username");
        String jwt = input.getHeaders().get("Authorization");

        String region = System.getenv("AWS_REGION");
        String userPoolID = System.getenv("COGNITO_USER_POOL_ID");
        String audience = System.getenv("COGNITO_APP_CLIENT_ID");

        String effect = "Allow";
        DecodedJWT decodedJWT = null;

        try {
            decodedJWT = JwtUtils.validateJwtForUser(jwt, region, userPoolID, audience);
        } catch (RuntimeException ex) {
            effect = "Deny";
            ex.printStackTrace();
        }



        return null;
    }
}
