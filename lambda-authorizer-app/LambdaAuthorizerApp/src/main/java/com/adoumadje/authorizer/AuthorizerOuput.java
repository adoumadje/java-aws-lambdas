package com.adoumadje.authorizer;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Getter;

import java.util.Map;

@Getter
@JsonDeserialize(builder = AuthorizerOuput.Builder.class)
public class AuthorizerOuput {
    @JsonProperty("principalId")
    private String principalId;
    @JsonProperty("policyDocument")
    private PolicyDocument policyDocument;
    @JsonProperty("context")
    private Map<String, String> context;
    @JsonProperty("usageIdentifierKey")
    private String usageIdentifierKey;

    public AuthorizerOuput(Builder builder) {
        this.principalId = builder.principalId;
        this.policyDocument = builder.policyDocument;
        this.context = builder.context;
        this.usageIdentifierKey = builder.usageIdentifierKey;
    }


    public static Builder builder() {
        return new Builder();
    }

    @JsonPOJOBuilder(withPrefix = "")
    public final static class Builder {
        private String principalId;
        private PolicyDocument policyDocument;
        private Map<String, String> context;
        private String usageIdentifierKey;

        public Builder principalId(String principalId) {
            this.principalId = principalId;
            return this;
        }

        public Builder policyDocument(PolicyDocument policyDocument) {
            this.policyDocument = policyDocument;
            return this;
        }

        public Builder context(Map<String, String> context) {
            this.context = context;
            return this;
        }

        public Builder usageIdentifierKey(String usageIdentifierKey) {
            this.usageIdentifierKey = usageIdentifierKey;
            return this;
        }

        public AuthorizerOuput build() {
                return new AuthorizerOuput(this);
        }
    }
}
