package com.flamingo.qa.api.service;

import com.flamingo.qa.api.client.GraphQlClient;
import com.flamingo.qa.helpers.common.TestLog;
import com.flamingo.qa.models.api.GraphQlResponse;
import io.qameta.allure.Step;

import java.util.HashMap;
import java.util.Map;

/**
 * Predefined movie GraphQL operations for positive and negative API coverage.
 */
public class MovieGraphQlService {

    private static final MovieGraphQlService SHARED = new MovieGraphQlService(GraphQlClient.shared());

    private static final String LIST_MOVIES = ""
            + "query ListMovies($first: Int!) {\n"
            + "  movies(first: $first) {\n"
            + "    id\n"
            + "    title\n"
            + "  }\n"
            + "}";

    private static final String MOVIE_BY_ID = ""
            + "query MovieById($id: ID!) {\n"
            + "  movie(where: { id: $id }) {\n"
            + "    id\n"
            + "    title\n"
            + "  }\n"
            + "}";

    private static final String MOVIE_WITH_FRAGMENT = ""
            + "fragment MovieCore on Movie {\n"
            + "  id\n"
            + "  title\n"
            + "  publishedBy {\n"
            + "    name\n"
            + "  }\n"
            + "}\n"
            + "query MoviesWithPublisher($first: Int!) {\n"
            + "  movies(first: $first) {\n"
            + "    ...MovieCore\n"
            + "  }\n"
            + "}";

    private final GraphQlClient graphQlClient;

    public MovieGraphQlService(GraphQlClient graphQlClient) {
        this.graphQlClient = graphQlClient;
    }

    /** Shared instance wired to {@link GraphQlClient#shared()}. */
    public static MovieGraphQlService shared() {
        return SHARED;
    }

    @Step("Query movies with first={first}")
    public GraphQlResponse listMovies(int first) {
        TestLog.step("GraphQL listMovies", "first=" + first);
        Map<String, Object> variables = new HashMap<>();
        variables.put("first", first);
        return graphQlClient.execute(LIST_MOVIES, variables);
    }

    @Step("Query movie by id={id}")
    public GraphQlResponse movieById(String id) {
        TestLog.step("GraphQL movieById", id);
        Map<String, Object> variables = new HashMap<>();
        variables.put("id", id);
        return graphQlClient.execute(MOVIE_BY_ID, variables);
    }

    @Step("Query movies with fragment and nested publishedBy")
    public GraphQlResponse moviesWithFragment(int first) {
        TestLog.step("GraphQL moviesWithFragment", "first=" + first);
        Map<String, Object> variables = new HashMap<>();
        variables.put("first", first);
        return graphQlClient.execute(MOVIE_WITH_FRAGMENT, variables);
    }

    @Step("Execute malformed GraphQL query")
    public GraphQlResponse malformedQuery() {
        TestLog.step("GraphQL malformedQuery");
        return graphQlClient.execute("{ movies(first:1 { id } }");
    }

    @Step("Request non-existent field on Movie")
    public GraphQlResponse nonExistentField() {
        TestLog.step("GraphQL nonExistentField");
        return graphQlClient.execute("{ movies(first:1){ id notARealField } }");
    }
}
