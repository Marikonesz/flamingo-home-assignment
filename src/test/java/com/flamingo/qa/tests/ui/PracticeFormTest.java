package com.flamingo.qa.tests.ui;

import com.flamingo.qa.models.ui.component.FormSuccessModal;
import com.flamingo.qa.models.ui.page.PracticeFormPage;
import com.flamingo.qa.tests.base.BaseUiTest;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.TmsLink;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("UI")
@Feature("DemoQA Practice Form")
class PracticeFormTest extends BaseUiTest {

    @Test
    @Tag("FLA-UI-001")
    @TmsLink("FLA-UI-001")
    @DisplayName("Submit practice form with file upload, date picker and state/city dropdowns")
    void shouldSubmitFormWithUploadDateAndDropdowns() {
        String firstName = "Ada";
        String lastName = "Lovelace";
        String email = uniqueEmail("form");
        String mobile = "1234567890";

        FormSuccessModal modal = new PracticeFormPage(page())
                .open()
                .fillPersonalDetails(firstName, lastName, email, "Female", mobile)
                .selectDateOfBirth("15", "June", "1990")
                .addSubject("Maths")
                .selectHobby("Reading")
                .uploadPicture(sampleUploadFile())
                .fillAddress("221B Baker Street")
                .selectStateAndCity("NCR", "Delhi")
                .submit();

        assertThat(modal.studentName()).isEqualTo(firstName + " " + lastName);
        assertThat(modal.studentEmail()).isEqualTo(email);
        assertThat(modal.gender()).isEqualTo("Female");
        assertThat(modal.mobile()).isEqualTo(mobile);
        assertThat(modal.dateOfBirth()).isEqualTo("15 June,1990");
        assertThat(modal.subjects()).contains("Maths");
        assertThat(modal.hobbies()).contains("Reading");
        assertThat(modal.picture()).isEqualTo("sample-upload.txt");
        assertThat(modal.address()).isEqualTo("221B Baker Street");
        assertThat(modal.stateAndCity()).isEqualTo("NCR Delhi");
    }

    @Test
    @Tag("FLA-UI-002")
    @TmsLink("FLA-UI-002")
    @DisplayName("Success modal appears after submitting a valid practice form")
    void shouldShowSuccessModalAfterSubmit() {
        FormSuccessModal modal = new PracticeFormPage(page())
                .open()
                .fillPersonalDetails("Alan", "Turing", uniqueEmail("modal"), "Male", "0987654321")
                .selectDateOfBirth("23", "June", "1912")
                .selectHobby("Sports")
                .uploadPicture(sampleUploadFile())
                .selectStateAndCity("Haryana", "Karnal")
                .submit();

        assertThat(modal.studentName()).isEqualTo("Alan Turing");
        assertThat(modal.gender()).isEqualTo("Male");
        assertThat(modal.dateOfBirth()).isEqualTo("23 June,1912");
        assertThat(modal.stateAndCity()).isEqualTo("Haryana Karnal");
    }

    private static Path sampleUploadFile() {
        try {
            return Paths.get(Objects.requireNonNull(
                    PracticeFormTest.class.getClassLoader().getResource("files/sample-upload.txt")).toURI());
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Test upload file is missing", e);
        }
    }

    private static String uniqueEmail(String prefix) {
        return prefix + "." + UUID.randomUUID().toString().substring(0, 8) + "@example.com";
    }
}
