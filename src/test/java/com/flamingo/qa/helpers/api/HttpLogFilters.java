package com.flamingo.qa.helpers.api;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * HTTP logging via SLF4J (picks up MDC testId) + Allure attachment filter.
 */
public final class HttpLogFilters {

    private static final Logger HTTP = LoggerFactory.getLogger("com.flamingo.qa.http");

    private HttpLogFilters() {
    }

    public static List<Filter> defaultFilters() {
        return List.of(new AllureRestAssured(), new Slf4jHttpFilter());
    }

    private static final class Slf4jHttpFilter implements Filter {

        @Override
        public Response filter(
                FilterableRequestSpecification requestSpec,
                FilterableResponseSpecification responseSpec,
                FilterContext ctx) {

            HTTP.info("REQUEST  {} {}", requestSpec.getMethod(), requestSpec.getURI());
            Object body = requestSpec.getBody();
            if (body != null) {
                HTTP.info("REQUEST  body: {}", body);
            }

            Response response = ctx.next(requestSpec, responseSpec);

            HTTP.info("RESPONSE {} {} {}", requestSpec.getMethod(), requestSpec.getURI(), response.statusCode());
            String responseBody = response.asString();
            if (responseBody != null && !responseBody.isBlank()) {
                HTTP.info("RESPONSE body: {}", responseBody);
            }
            return response;
        }
    }
}
