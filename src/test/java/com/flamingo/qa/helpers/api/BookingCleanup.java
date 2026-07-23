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

    public void track(int bookingId) {
        bookingIds.push(bookingId);
    }

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
