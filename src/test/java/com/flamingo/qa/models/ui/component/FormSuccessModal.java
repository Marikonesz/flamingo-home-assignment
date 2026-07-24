package com.flamingo.qa.models.ui.component;

import com.flamingo.qa.helpers.common.TestLog;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import io.qameta.allure.Step;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Success dialog shown after submitting the student practice form.
 */
public class FormSuccessModal {

    private final Locator dialog;
    private final Locator title;
    private final Locator rows;

    public FormSuccessModal(Page page) {
        this.dialog = page.locator(".modal-content");
        this.title = page.locator("#example-modal-sizes-title-lg");
        this.rows = page.locator(".modal-content table tbody tr");
    }

    @Step("Wait for success modal")
    public FormSuccessModal waitUntilVisible() {
        TestLog.step("Wait for success modal");
        assertThat(dialog).isVisible();
        assertThat(title).hasText("Thanks for submitting the form");
        return this;
    }

    @Step("Assert success modal is not shown")
    public FormSuccessModal assertNotVisible() {
        TestLog.step("Assert success modal is not shown");
        assertThat(dialog).isHidden();
        return this;
    }

    public String studentName() {
        return valueFor("Student Name");
    }

    public String studentEmail() {
        return valueFor("Student Email");
    }

    public String gender() {
        return valueFor("Gender");
    }

    public String mobile() {
        return valueFor("Mobile");
    }

    public String dateOfBirth() {
        return valueFor("Date of Birth");
    }

    public String subjects() {
        return valueFor("Subjects");
    }

    public String hobbies() {
        return valueFor("Hobbies");
    }

    public String picture() {
        return valueFor("Picture");
    }

    public String address() {
        return valueFor("Address");
    }

    public String stateAndCity() {
        return valueFor("State and City");
    }

    private String valueFor(String label) {
        Locator row = rows.filter(new Locator.FilterOptions().setHasText(label)).first();
        assertThat(row).isVisible();
        String value = row.locator("td").nth(1).innerText().trim();
        TestLog.step("Read modal field", label + " = " + value);
        return value;
    }
}
