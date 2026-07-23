package com.flamingo.qa.api.service;

import com.flamingo.qa.api.client.RestClient;
import com.flamingo.qa.helpers.common.TestLog;
import com.flamingo.qa.models.api.AuthRequest;
import com.flamingo.qa.models.api.AuthResponse;
import io.qameta.allure.Step;
import io.restassured.response.Response;

public class AuthService {

    private static final AuthService SHARED = new AuthService(RestClient.shared());

    private static final Object TOKEN_LOCK = new Object();
    private static volatile String cachedAdminToken;

    private final RestClient restClient;

    public AuthService(RestClient restClient) {
        this.restClient = restClient;
    }

    public static AuthService shared() {
        return SHARED;
    }

    @Step("Authenticate as {username}")
    public AuthResponse createToken(String username, String password) {
        TestLog.step("API Authenticate", username);
        AuthRequest request = AuthRequest.builder()
                .username(username)
                .password(password)
                .build();
        Response response = restClient.post("/auth", request);
        response.then().statusCode(200);
        return response.as(AuthResponse.class);
    }

    /**
     * Returns a cached admin token for the JVM (shard). First call authenticates once.
     */
    @Step("Get admin auth token")
    public String getAdminToken() {
        String token = cachedAdminToken;
        if (token != null) {
            TestLog.step("API Get admin token (cached)");
            return token;
        }
        synchronized (TOKEN_LOCK) {
            if (cachedAdminToken == null) {
                TestLog.step("API Get admin token (authenticate)");
                cachedAdminToken = createToken("admin", "password123").getToken();
            } else {
                TestLog.step("API Get admin token (cached)");
            }
            return cachedAdminToken;
        }
    }
}
