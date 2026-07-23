package com.flamingo.qa.tests.api.graphql;

import com.flamingo.qa.models.api.GraphQlResponse;
import com.flamingo.qa.tests.base.BaseApiTest;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("API")
@Feature("Hygraph Video GraphQL — negative")
class MovieNegativeTest extends BaseApiTest {

    @Test
    @Tag("FLA-GQL-005")
    @TmsLink("FLA-GQL-005")
    @DisplayName("Invalid movie ID returns HTTP 200 with data.movie = null")
    void shouldReturnNullForNonExistentMovieId() {
        GraphQlResponse response = movieService.movieById("clxxxxxxxxxxxxxxxxxxxxxxxxx");

        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.getData()).isNotNull();
        assertThat(response.getData().get("movie").isNull()).isTrue();
    }

    @Test
    @Tag("FLA-GQL-006")
    @TmsLink("FLA-GQL-006")
    @DisplayName("Malformed query returns HTTP 400 with errors and null data")
    void shouldReturnErrorsForMalformedQuery() {
        GraphQlResponse response = movieService.malformedQuery();

        assertThat(response.getStatusCode()).isEqualTo(400);
        assertThat(response.getErrors()).isNotEmpty();
        assertThat(response.getErrors().get(0).getMessage()).isNotBlank();
        assertThat(response.getData() == null || response.getData().isNull()).isTrue();
    }

    @Test
    @Tag("FLA-GQL-007")
    @TmsLink("FLA-GQL-007")
    @DisplayName("Non-existent field returns HTTP 400 validation error")
    void shouldReturnValidationErrorForUnknownField() {
        GraphQlResponse response = movieService.nonExistentField();

        assertThat(response.getStatusCode()).isEqualTo(400);
        assertThat(response.getErrors()).isNotEmpty();
        assertThat(response.getErrors().get(0).getMessage())
                .containsIgnoringCase("notARealField");
        assertThat(response.getData() == null || response.getData().isNull()).isTrue();
    }
}
