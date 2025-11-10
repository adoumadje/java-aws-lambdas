package com.adoumadje.authorizer;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.List;

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

        String version = "2012-10-17";

        String action = "execute-api:Invoke";
        String effect = "Allow";
        DecodedJWT decodedJWT = null;

        try {
            decodedJWT = JwtUtils.validateJwtForUser(jwt, region, userPoolID, username, audience);
            username = decodedJWT.getSubject();
        } catch (RuntimeException ex) {
            effect = "Deny";
            ex.printStackTrace();
        }

        APIGatewayProxyRequestEvent.ProxyRequestContext proxyRequestContext = input.getRequestContext();

        String arn = String.format("arn:aws:execute-api:%s:%s:%s/%s/%s/%s",
                region,
                proxyRequestContext.getAccountId(),
                proxyRequestContext.getApiId(),
                proxyRequestContext.getStage(),
                proxyRequestContext.getHttpMethod(), "*");

        Statement statement = Statement.builder()
                .action(action)
                .effect(effect)
                .resource(arn).build();

        PolicyDocument policyDocument = PolicyDocument.builder()
                .version(version)
                .statements(List.of(statement))
                .build();

        AuthorizerOuput authorizerOuput = AuthorizerOuput.builder()
                .principalId(username)
                .policyDocument(policyDocument)
                .build();

        return authorizerOuput;
    }
}
