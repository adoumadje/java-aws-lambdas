package com.adoumadje.authorizer;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.RSAKeyProvider;

public class JwtUtils {

    public static DecodedJWT validateJwtForUser(String jwt, String region, String userPoolID,
                                                String principalId, String audience) {
        RSAKeyProvider keyProvider = new AwsCognitoRSAKeyProvider(region, userPoolID);

        Algorithm algorithm = Algorithm.RSA256(keyProvider);

        String issuer = String.format("https://cognito-idp.%s.amazonaws.com/%s", region, userPoolID);

        JWTVerifier jwtVerifier = JWT.require(algorithm)
                .withSubject(principalId)
                .withAudience(audience)
                .withIssuer(issuer)
                .withClaim("token_use", "id")
                .build();

        return jwtVerifier.verify(jwt);
    }
}
