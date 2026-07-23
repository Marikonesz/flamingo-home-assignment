package com.flamingo.qa.tests.api.graphql;

import com.flamingo.qa.models.api.GraphQlResponse;
import com.flamingo.qa.tests.base.BaseApiTest;
import com.fasterxml.jackson.databind.JsonNode;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("API")
@Feature("Hygraph Video GraphQL — positive")
class MoviePositiveTest extends BaseApiTest {

    @Test
    @Tag("FLA-GQL-001")
    @TmsLink("FLA-GQL-001")
    @DisplayName("Query movies list with pagination limit via GraphQL variables")
    void shouldListMoviesWithPaginationVariables() {
        GraphQlResponse response = movieService.listMovies(2);

        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.getErrors()).isNullOrEmpty();
        JsonNode movies = response.getData().get("movies");
        assertThat(movies.isArray()).isTrue();
        assertThat(movies.size()).isBetween(1, 2);
        assertThat(movies.get(0).get("id").asText()).isNotBlank();
        assertThat(movies.get(0).get("title").asText()).isNotBlank();
    }

    @Test
    @Tag("FLA-GQL-002")
    @TmsLink("FLA-GQL-002")
    @DisplayName("Query a single movie by ID using GraphQL variables")
    void shouldQueryMovieByIdWithVariables() {
        GraphQlResponse list = movieService.listMovies(1);
        String id = list.getData().get("movies").get(0).get("id").asText();

        GraphQlResponse response = movieService.movieById(id);

        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.getErrors()).isNullOrEmpty();
        assertThat(response.getData().get("movie").get("id").asText()).isEqualTo(id);
        assertThat(response.getData().get("movie").get("title").asText()).isNotBlank();
    }

    @Test
    @Tag("FLA-GQL-003")
    @TmsLink("FLA-GQL-003")
    @DisplayName("Query movies using a fragment and nested publishedBy.name")
    void shouldQueryWithFragmentAndNestedFields() {
        GraphQlResponse response = movieService.moviesWithFragment(1);

        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.getErrors()).isNullOrEmpty();
        JsonNode movie = response.getData().get("movies").get(0);
        assertThat(movie.get("title").asText()).isNotBlank();
        assertThat(movie.get("publishedBy").get("name").asText()).isNotBlank();
    }

    @Test
    @Tag("FLA-GQL-004")
    @TmsLink("FLA-GQL-004")
    @DisplayName("Different GraphQL variable values change pagination result size")
    void shouldRespectDifferentVariableValues() {
        GraphQlResponse one = movieService.listMovies(1);
        GraphQlResponse two = movieService.listMovies(2);

        assertThat(one.getStatusCode()).isEqualTo(200);
        assertThat(two.getStatusCode()).isEqualTo(200);
        assertThat(one.getData().get("movies").size()).isEqualTo(1);
        assertThat(two.getData().get("movies").size()).isGreaterThanOrEqualTo(1);
        assertThat(two.getData().get("movies").size()).isLessThanOrEqualTo(2);
        assertThat(two.getData().get("movies").size())
                .as("Increasing $first via variables should allow a larger page")
                .isGreaterThanOrEqualTo(one.getData().get("movies").size());
    }
}
