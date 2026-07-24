package com.flamingo.qa.api.service;

import com.flamingo.qa.api.client.RestClient;
import com.flamingo.qa.helpers.common.TestLog;
import com.flamingo.qa.models.api.Booking;
import com.flamingo.qa.models.api.CreateBookingResponse;
import io.qameta.allure.Step;
import io.restassured.response.Response;

/**
 * Restful Booker booking CRUD. Happy-path methods assert HTTP 200 before returning bodies.
 */
public class BookingService {

    private static final BookingService SHARED = new BookingService(RestClient.shared());

    private final RestClient restClient;

    public BookingService(RestClient restClient) {
        this.restClient = restClient;
    }

    /** Shared instance wired to {@link RestClient#shared()}. */
    public static BookingService shared() {
        return SHARED;
    }

    @Step("Create booking")
    public CreateBookingResponse createBooking(Booking booking) {
        TestLog.step("API Create booking", booking.getFirstname() + " " + booking.getLastname());
        Response response = restClient.post("/booking", booking);
        response.then().statusCode(200);
        return response.as(CreateBookingResponse.class);
    }

    /** Raw GET for status/body assertions without enforcing 200. */
    @Step("Get booking response by id={bookingId}")
    public Response getBookingResponse(int bookingId) {
        TestLog.step("API Get booking response", bookingId);
        return restClient.get("/booking/" + bookingId);
    }

    @Step("Get booking by id={bookingId}")
    public Booking getBooking(int bookingId) {
        Response response = getBookingResponse(bookingId);
        response.then().statusCode(200);
        return response.as(Booking.class);
    }

    @Step("Update booking id={bookingId}")
    public Booking updateBooking(int bookingId, Booking booking, String token) {
        TestLog.step("API Update booking", bookingId);
        Response response = restClient.put("/booking/" + bookingId, booking, token);
        response.then().statusCode(200);
        return response.as(Booking.class);
    }

    @Step("Delete booking id={bookingId}")
    public Response deleteBooking(int bookingId, String token) {
        TestLog.step("API Delete booking", bookingId);
        return restClient.delete("/booking/" + bookingId, token);
    }
}
