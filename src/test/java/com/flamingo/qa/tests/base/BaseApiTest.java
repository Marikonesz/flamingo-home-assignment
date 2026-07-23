package com.flamingo.qa.tests.base;

import com.flamingo.qa.api.service.AuthService;
import com.flamingo.qa.api.service.BookingService;
import com.flamingo.qa.api.service.MovieGraphQlService;
import com.flamingo.qa.helpers.common.TestContextExtension;
import com.flamingo.qa.helpers.sharding.Sharded;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Shared setup for all API tests: tags, sharding, shared services, test-id logging.
 */
@Tag("api")
@Sharded
@ExtendWith(TestContextExtension.class)
public abstract class BaseApiTest {

    protected final AuthService authService = AuthService.shared();
    protected final BookingService bookingService = BookingService.shared();
    protected final MovieGraphQlService movieService = MovieGraphQlService.shared();
}
