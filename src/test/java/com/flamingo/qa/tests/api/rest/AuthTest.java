package com.flamingo.qa.tests.api.rest;

import com.flamingo.qa.tests.base.BaseApiTest;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("API")
@Feature("Restful Booker")
class AuthTest extends BaseApiTest {

    @Test
    @Tag("FLA-REST-001")
    @TmsLink("FLA-REST-001")
    @DisplayName("POST /auth returns a non-blank token for valid credentials")
    void shouldCreateAuthToken() {
        // Hit /auth directly so this test does not depend on the shared token cache.
        String token = authService.createToken("admin", "password123").getToken();

        assertThat(token)
                .as("Auth token should be present")
                .isNotBlank();
    }
}
