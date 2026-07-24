package com.flamingo.qa.helpers.api;

import com.flamingo.qa.api.service.AuthService;
import com.flamingo.qa.api.service.BookingService;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Tracks bookings created during a test and deletes them afterwards (best-effort).
 */
public final class BookingCleanup {

    private final Deque<Integer> bookingIds = new ArrayDeque<>();
    private final AuthService authService;
    private final BookingService bookingService;

    public BookingCleanup(AuthService authService, BookingService bookingService) {
        this.authService = authService;
        this.bookingService = bookingService;
    }

    /** Registers a booking id for LIFO deletion in {@link #cleanup()}. */
    public void track(int bookingId) {
        bookingIds.push(bookingId);
    }

    /** Deletes tracked bookings in reverse creation order; failures are ignored (best-effort). */
    public void cleanup() {
        String token = null;
        while (!bookingIds.isEmpty()) {
            int bookingId = bookingIds.pop();
            try {
                if (token == null) {
                    token = authService.getAdminToken();
                }
                bookingService.deleteBooking(bookingId, token);
            } catch (Exception ignored) {
                // best-effort; booking may already be deleted or API may reset
            }
        }
    }
}
