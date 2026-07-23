package com.flamingo.qa.models.ui.page;

import com.flamingo.qa.helpers.common.TestLog;
import com.flamingo.qa.models.ui.component.FormSuccessModal;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.SelectOption;
import io.qameta.allure.Step;

import java.nio.file.Path;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class PracticeFormPage extends BasePage {

    private static final String PATH = "/automation-practice-form";

    private final Locator heading;
    private final Locator firstNameInput;
    private final Locator lastNameInput;
    private final Locator emailInput;
    private final Locator genderGroup;
    private final Locator mobileInput;
    private final Locator dateOfBirthInput;
    private final Locator monthSelect;
    private final Locator yearSelect;
    private final Locator subjectsInput;
    private final Locator hobbiesGroup;
    private final Locator uploadPictureInput;
    private final Locator addressInput;
    private final Locator stateDropdown;
    private final Locator stateInput;
    private final Locator cityDropdown;
    private final Locator cityInput;
    private final Locator submitButton;

    public PracticeFormPage(Page page) {
        super(page);
        this.heading = page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Practice Form"));
        this.firstNameInput = page.getByPlaceholder("First Name");
        this.lastNameInput = page.getByPlaceholder("Last Name");
        this.emailInput = page.getByPlaceholder("name@example.com");
        this.genderGroup = page.locator("#genterWrapper");
        this.mobileInput = page.getByPlaceholder("Mobile Number");
        this.dateOfBirthInput = page.locator("#dateOfBirthInput");
        this.monthSelect = page.locator(".react-datepicker__month-select");
        this.yearSelect = page.locator(".react-datepicker__year-select");
        this.subjectsInput = page.locator("#subjectsInput");
        this.hobbiesGroup = page.locator("#hobbiesWrapper");
        this.uploadPictureInput = page.locator("#uploadPicture");
        this.addressInput = page.getByPlaceholder("Current Address");
        this.stateDropdown = page.locator("#state");
        this.stateInput = page.locator("#state input");
        this.cityDropdown = page.locator("#city");
        this.cityInput = page.locator("#city input");
        this.submitButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Submit"));
    }

    @Step("Open Practice Form page")
    public PracticeFormPage open() {
        TestLog.step("Open Practice Form page");
        open(PATH);
        assertThat(heading).isVisible();
        assertThat(firstNameInput).isVisible();
        return this;
    }

    @Step("Fill personal details")
    public PracticeFormPage fillPersonalDetails(String firstName, String lastName, String email, String gender, String mobile) {
        TestLog.step("Fill personal details", firstName + " " + lastName, gender);
        firstNameInput.fill(firstName);
        lastNameInput.fill(lastName);
        emailInput.fill(email);
        genderOption(gender).click();
        mobileInput.fill(mobile);
        return this;
    }

    @Step("Select date of birth: {day} {month} {year}")
    public PracticeFormPage selectDateOfBirth(String day, String month, String year) {
        TestLog.step("Select date of birth", day + " " + month + " " + year);
        dateOfBirthInput.click();
        monthSelect.selectOption(new SelectOption().setLabel(month));
        yearSelect.selectOption(year);
        dayOption(day).click();
        return this;
    }

    @Step("Add subject: {subject}")
    public PracticeFormPage addSubject(String subject) {
        TestLog.step("Add subject", subject);
        subjectsInput.fill(subject);
        subjectsInput.press("Enter");
        return this;
    }

    @Step("Select hobby: {hobby}")
    public PracticeFormPage selectHobby(String hobby) {
        TestLog.step("Select hobby", hobby);
        hobbyOption(hobby).click();
        return this;
    }

    @Step("Upload picture: {file}")
    public PracticeFormPage uploadPicture(Path file) {
        TestLog.step("Upload picture", file.getFileName());
        uploadPictureInput.setInputFiles(file);
        return this;
    }

    @Step("Fill address")
    public PracticeFormPage fillAddress(String address) {
        TestLog.step("Fill address", address);
        addressInput.fill(address);
        return this;
    }

    @Step("Select state={state} and city={city}")
    public PracticeFormPage selectStateAndCity(String state, String city) {
        TestLog.step("Select state and city", state, city);
        stateDropdown.click();
        stateInput.fill(state);
        stateInput.press("Enter");

        cityDropdown.click();
        cityInput.fill(city);
        cityInput.press("Enter");
        return this;
    }

    @Step("Submit practice form")
    public FormSuccessModal submit() {
        TestLog.step("Submit practice form");
        submitButton.click();
        return new FormSuccessModal(page).waitUntilVisible();
    }

    private Locator genderOption(String gender) {
        return genderGroup.getByText(gender, new Locator.GetByTextOptions().setExact(true));
    }

    private Locator hobbyOption(String hobby) {
        return hobbiesGroup.getByText(hobby, new Locator.GetByTextOptions().setExact(true));
    }

    private Locator dayOption(String day) {
        String dayToken = String.format("%03d", Integer.parseInt(day));
        return page.locator(".react-datepicker__day--" + dayToken + ":not(.react-datepicker__day--outside-month)");
    }
}
