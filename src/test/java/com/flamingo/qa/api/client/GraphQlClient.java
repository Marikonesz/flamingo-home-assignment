package com.flamingo.qa.api.client;

import com.flamingo.qa.config.api.ApiConfig;
import com.flamingo.qa.helpers.api.HttpLogFilters;
import com.flamingo.qa.helpers.api.JsonHelper;
import com.flamingo.qa.models.api.GraphQlRequest;
import com.flamingo.qa.models.api.GraphQlResponse;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Collections;
import java.util.Map;

/**
 * Stateless GraphQL client shared per JVM (shard). Safe for parallel threads.
 */
public class GraphQlClient {

    private static final GraphQlClient SHARED = new GraphQlClient();

    private final RequestSpecification baseSpec;

    public GraphQlClient() {
        RequestSpecBuilder builder = new RequestSpecBuilder()
                .setBaseUri(ApiConfig.graphqlBaseUrl())
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json");
        HttpLogFilters.defaultFilters().forEach(builder::addFilter);
        this.baseSpec = builder.build();
    }

    /** One client per JVM (shard); thread-safe because each call builds an isolated request. */
    public static GraphQlClient shared() {
        return SHARED;
    }

    public GraphQlResponse execute(String query) {
        return execute(query, null);
    }

    /** Posts the query envelope and attaches the HTTP status to the parsed response. */
    public GraphQlResponse execute(String query, Map<String, Object> variables) {
        GraphQlRequest request = GraphQlRequest.builder()
                .query(query)
                .variables(variables)
                .build();

        Response response = RestAssured.given()
                .spec(baseSpec)
                .body(JsonHelper.toJson(request))
                .when()
                .post()
                .then()
                .extract()
                .response();

        return toGraphQlResponse(response);
    }

    private static GraphQlResponse toGraphQlResponse(Response response) {
        String body = response.asString();
        GraphQlResponse parsed = JsonHelper.fromJson(body, GraphQlResponse.class);
        if (parsed == null) {
            parsed = new GraphQlResponse();
        }
        parsed.setStatusCode(response.statusCode());
        if (parsed.getErrors() == null) {
            parsed.setErrors(Collections.emptyList());
        }
        return parsed;
    }
}
