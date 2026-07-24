package com.flamingo.qa.api.client;

import com.flamingo.qa.config.api.ApiConfig;
import com.flamingo.qa.helpers.api.HttpLogFilters;
import com.flamingo.qa.helpers.api.JsonHelper;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

/**
 * Stateless REST client shared per JVM (shard). Safe for parallel threads.
 */
public class RestClient {

    private static final RestClient SHARED = new RestClient();

    private final RequestSpecification baseSpec;

    public RestClient() {
        // Explicit header strings avoid Content-Type charset quirks that can trigger HTTP 418.
        RequestSpecBuilder builder = new RequestSpecBuilder()
                .setBaseUri(ApiConfig.restBaseUrl())
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .addHeader("User-Agent", "flamingo-qa-automation/1.0");
        HttpLogFilters.defaultFilters().forEach(builder::addFilter);
        this.baseSpec = builder.build();
    }

    /** One client per JVM (shard); thread-safe because each call builds an isolated request. */
    public static RestClient shared() {
        return SHARED;
    }

    public Response post(String path, Object body) {
        return RestAssured.given()
                .spec(baseSpec)
                .body(JsonHelper.toJson(body))
                .when()
                .post(path)
                .then()
                .extract()
                .response();
    }

    public Response get(String path) {
        return RestAssured.given()
                .spec(baseSpec)
                .when()
                .get(path)
                .then()
                .extract()
                .response();
    }

    /** Authenticated update; Restful Booker expects the token in a {@code Cookie} header. */
    public Response put(String path, Object body, String token) {
        return RestAssured.given()
                .spec(baseSpec)
                .header("Cookie", "token=" + token)
                .body(JsonHelper.toJson(body))
                .when()
                .put(path)
                .then()
                .extract()
                .response();
    }

    /** Authenticated delete; token is sent as {@code Cookie: token=...}. */
    public Response delete(String path, String token) {
        return RestAssured.given()
                .spec(baseSpec)
                .header("Cookie", "token=" + token)
                .when()
                .delete(path)
                .then()
                .extract()
                .response();
    }
}
