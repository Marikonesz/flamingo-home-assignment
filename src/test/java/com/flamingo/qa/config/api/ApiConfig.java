package com.flamingo.qa.config.api;

import com.flamingo.qa.config.ConfigProperties;

/** REST and GraphQL base URLs resolved via {@link com.flamingo.qa.config.ConfigProperties}. */
public final class ApiConfig {

    private ApiConfig() {
    }

    public static String restBaseUrl() {
        return ConfigProperties.get("rest.baseUrl");
    }

    public static String graphqlBaseUrl() {
        return ConfigProperties.get("graphql.baseUrl");
    }
}
