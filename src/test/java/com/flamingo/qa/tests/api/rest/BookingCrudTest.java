package com.flamingo.qa.tests.api.rest;

import com.flamingo.qa.models.api.Booking;
import com.flamingo.qa.models.api.BookingDates;
import com.flamingo.qa.models.api.CreateBookingResponse;
import com.flamingo.qa.tests.base.BaseBookingApiTest;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("API")
@Feature("Restful Booker CRUD")
class BookingCrudTest extends BaseBookingApiTest {

    @Test
    @Tag("FLA-REST-002")
    @TmsLink("FLA-REST-002")
    @DisplayName("Create booking and retrieve it by id")
    void shouldCreateAndGetBooking() {
        Booking payload = uniqueBooking("Create");
        CreateBookingResponse created = bookingService.createBooking(payload);
        bookingCleanup.track(created.getBookingid());

        assertThat(created.getBookingid()).isPositive();
        assertThat(created.getBooking()).usingRecursiveComparison().isEqualTo(payload);

        Booking fetched = bookingService.getBooking(created.getBookingid());
        assertThat(fetched).usingRecursiveComparison().isEqualTo(payload);
    }

    @Test
    @Tag("FLA-REST-003")
    @TmsLink("FLA-REST-003")
    @DisplayName("Create, get and update booking")
    void shouldCreateGetAndUpdateBooking() {
        String token = authService.getAdminToken();
        Booking createdPayload = uniqueBooking("Crud");

        CreateBookingResponse created = bookingService.createBooking(createdPayload);
        int bookingId = created.getBookingid();
        bookingCleanup.track(bookingId);

        Booking fetched = bookingService.getBooking(bookingId);
        assertThat(fetched.getFirstname()).isEqualTo(createdPayload.getFirstname());

        Booking updatedPayload = uniqueBooking("Updated");
        Booking updated = bookingService.updateBooking(bookingId, updatedPayload, token);
        assertThat(updated).usingRecursiveComparison().isEqualTo(updatedPayload);
    }

    @Test
    @Tag("FLA-REST-004")
    @TmsLink("FLA-REST-004")
    @DisplayName("Delete booking returns 201 and booking is no longer retrievable")
    void shouldDeleteBooking() {
        String token = authService.getAdminToken();
        CreateBookingResponse created = bookingService.createBooking(uniqueBooking("Delete"));
        int bookingId = created.getBookingid();
        bookingCleanup.track(bookingId);

        Response deleteResponse = bookingService.deleteBooking(bookingId, token);
        assertThat(deleteResponse.statusCode()).isEqualTo(201);

        Response afterDelete = bookingService.getBookingResponse(bookingId);
        assertThat(afterDelete.statusCode()).isIn(404, 405);
    }

    private static Booking uniqueBooking(String prefix) {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        int price = 100 + Math.floorMod(suffix.hashCode(), 400);
        return Booking.builder()
                .firstname(prefix + "First" + suffix)
                .lastname(prefix + "Last" + suffix)
                .totalprice(price)
                .depositpaid(true)
                .bookingdates(BookingDates.builder()
                        .checkin("2025-01-10")
                        .checkout("2025-01-15")
                        .build())
                .additionalneeds("Breakfast")
                .build();
    }
}
