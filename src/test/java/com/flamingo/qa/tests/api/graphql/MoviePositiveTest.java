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
import static org.assertj.core.api.SoftAssertions.assertSoftly;

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
        assertSoftly(softly -> {
            softly.assertThat(movies.isArray()).isTrue();
            softly.assertThat(movies.size()).isBetween(1, 2);
            softly.assertThat(movies.get(0).get("id").asText()).isNotBlank();
            softly.assertThat(movies.get(0).get("title").asText()).isNotBlank();
        });
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
        assertSoftly(softly -> {
            softly.assertThat(response.getData().get("movie").get("id").asText()).isEqualTo(id);
            softly.assertThat(response.getData().get("movie").get("title").asText()).isNotBlank();
        });
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
        assertSoftly(softly -> {
            softly.assertThat(movie.get("title").asText()).isNotBlank();
            softly.assertThat(movie.get("publishedBy").get("name").asText()).isNotBlank();
        });
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
        assertSoftly(softly -> {
            softly.assertThat(one.getData().get("movies").size()).isEqualTo(1);
            softly.assertThat(two.getData().get("movies").size()).isGreaterThanOrEqualTo(1);
            softly.assertThat(two.getData().get("movies").size()).isLessThanOrEqualTo(2);
            softly.assertThat(two.getData().get("movies").size())
                    .as("Increasing $first via variables should allow a larger page")
                    .isGreaterThanOrEqualTo(one.getData().get("movies").size());
        });
    }
}
