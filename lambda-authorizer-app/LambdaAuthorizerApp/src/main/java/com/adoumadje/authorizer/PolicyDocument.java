package com.adoumadje.authorizer;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@JsonDeserialize(builder = PolicyDocument.Builder.class)
public class PolicyDocument {
    @JsonProperty("Version")
    private String version;
    @JsonProperty("Statement")
    private List<Statement> statements;

    public PolicyDocument(Builder builder) {
        this.version = builder.version;
        this.statements = builder.statements;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String version;
        private List<Statement> statements;

        public Builder version(String version) {
            this.version = version;
            return this;
        }

        public Builder statements(List<Statement> statements) {
            this.statements = statements;
            return this;
        }

        public PolicyDocument build() {
            return new PolicyDocument(this);
        }
    }
}
