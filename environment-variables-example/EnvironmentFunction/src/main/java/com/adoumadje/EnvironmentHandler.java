package com.adoumadje;

import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import com.amazonaws.services.kms.model.DecryptRequest;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.util.Base64;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Handler for requests to Lambda function.
 */
public class EnvironmentHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        LambdaLogger logger = context.getLogger();

        String myEnvVar1 = decryptEnvVariable("MY_VARIABLE_1");
        String myEnvVar2 = decryptEnvVariable("MY_VARIABLE_2");

        logger.log("MY_VARIABLE_1 = " + myEnvVar1);
        logger.log("MY_VARIABLE_2 = " + myEnvVar2);

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        return response.withHeaders(headers).withStatusCode(200).withBody("{}");
    }

    private String decryptEnvVariable(String varName) {
        System.out.printf("Decrypting key {%s}...%n", varName);
        byte[] encryptedKey = Base64.decode(System.getenv(varName));
        Map<String, String> encryptionContext = new HashMap<>();
        encryptionContext.put("LambdaFunctionName",
                System.getenv("AWS_LAMBDA_FUNCTION_NAME"));

        AWSKMS client = AWSKMSClientBuilder.defaultClient();

        DecryptRequest request = new DecryptRequest()
                .withCiphertextBlob(ByteBuffer.wrap(encryptedKey))
                .withEncryptionContext(encryptionContext);

        ByteBuffer plainTextKey = client.decrypt(request).getPlaintext();

        return new String(plainTextKey.array(), StandardCharsets.UTF_8);
    }
}
