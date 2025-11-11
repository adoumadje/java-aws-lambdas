package com.adoumadje.authorizer;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Getter;

import java.util.List;

@Getter
@JsonDeserialize(builder = PolicyDocument.Builder.class)
public class PolicyDocument {
    @JsonProperty("Version")
    private String Version;
    @JsonProperty("Statement")
    private List<Statement> Statement;

    public PolicyDocument(Builder builder) {
        this.Version = builder.version;
        this.Statement = builder.statements;
    }

    public static Builder builder() {
        return new Builder();
    }

    @JsonPOJOBuilder(withPrefix = "")
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
