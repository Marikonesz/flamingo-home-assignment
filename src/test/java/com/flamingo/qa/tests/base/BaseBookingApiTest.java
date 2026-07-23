package com.flamingo.qa.tests.base;

import com.flamingo.qa.helpers.api.BookingCleanup;
import org.junit.jupiter.api.AfterEach;

/**
 * Base for Restful Booker tests that create bookings and need automatic cleanup.
 */
public abstract class BaseBookingApiTest extends BaseApiTest {

    protected final BookingCleanup bookingCleanup = new BookingCleanup(authService, bookingService);

    @AfterEach
    void cleanupCreatedBookings() {
        bookingCleanup.cleanup();
    }
}
