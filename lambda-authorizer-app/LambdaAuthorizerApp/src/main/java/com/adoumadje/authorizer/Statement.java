package com.adoumadje.authorizer;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;

@Getter
@JsonDeserialize(builder = Statement.Builder.class)
public class Statement {
    @JsonProperty("Action")
    private String action;
    @JsonProperty("Effect")
    private String effect;
    @JsonProperty("Resource")
    private String resource;

    private Statement(Builder builder) {
        this.action = builder.action;
        this.effect = builder.effect;
        this.resource = builder.resource;
    }


    public Builder builder() {
        return new Builder();
    }

    public final static class Builder {
        private String action;
        private String effect;
        private String resource;

        public Builder action(String action) {
            this.action = action;
            return this;
        }

        public Builder effect(String effect) {
            this.effect = effect;
            return this;
        }

        public Builder resource(String resource) {
            this.resource = resource;
            return this;
        }

        public Statement build() {
            return new Statement(this);
        }
    }
}
