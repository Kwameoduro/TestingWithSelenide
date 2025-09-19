package com.selenide.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class LoginPage {

    // ===== Locators =====
    private final SelenideElement loginButton        = $("#login-button");
    private final SelenideElement usernameField      = $("#user-name");
    private final SelenideElement passwordField      = $("#password");
    private final SelenideElement errorMessageBox    = $(".error-message-container");
    private final SelenideElement errorMessage       = $(".error-message-container h3");
    private final SelenideElement closeErrorButton   = $(".error-button");
    private final SelenideElement inventoryContainer = $("#inventory_container");
    private final SelenideElement productsTitle      = $(".title"); // "Products"
    private final SelenideElement errorCloseBtn   = $("button.error-button");
    private final SelenideElement errorText = $("[data-test='error']");




    // Open the login page safely

    public LoginPage openPage() {
        open(""); // baseUrl defined in config
        loginButton.shouldBe(Condition.visible); // wait for page
        return this;
    }


     //  Login with valid credentials
     //  Assumes a successful login and waits for product page.

    public ProductsPage login(String username, String password) {
        setUsername(username);
        setPassword(password);
        clickLogin();
        inventoryContainer.shouldBe(Condition.visible);
        return new ProductsPage();
    }

    /**
     * Attempt login with invalid credentials
     * Stays on login page, used for negative tests.
     */
    public LoginPage attemptLogin(String username, String password) {
        setUsername(username);
        setPassword(password);
        clickLogin();
        return this;
    }

    // ===== Fluent Setters =====
    public LoginPage enterUsername(String username) {
        usernameField.shouldBe(Condition.visible).clear();
        usernameField.setValue(username);
        return this;
    }

    public LoginPage enterPassword(String password) {
        passwordField.shouldBe(Condition.visible).clear();
        passwordField.setValue(password);
        return this;
    }

    public LoginPage setUsername(String username) {
        usernameField.shouldBe(Condition.visible).clear();
        usernameField.setValue(username);
        return this;
    }

    public LoginPage setPassword(String password) {
        passwordField.shouldBe(Condition.visible).clear();
        passwordField.setValue(password);
        return this;
    }

    public LoginPage clickLogin() {
        loginButton.shouldBe(Condition.visible).click();
        return this;
    }

    // ===== Utility Methods =====
    public boolean isDashboardVisible() {
        return inventoryContainer.exists() && inventoryContainer.is(Condition.visible);
    }

    public boolean isErrorMessageVisible() {
        return errorMessageBox.exists() && errorMessageBox.is(Condition.visible);
    }

    public void closeErrorMessage() {
        if (isErrorMessageVisible()) {
            closeErrorButton.shouldBe(Condition.visible).click();
            errorMessageBox.shouldNotBe(Condition.visible);
        }
    }

    public boolean isLoginButtonEnabled() {
        return loginButton.isEnabled(); // no forced wait
    }

    public String getErrorMessageText() {
        return errorMessage.shouldBe(Condition.visible).getText();
    }

    public String getPasswordFieldType() {
        return passwordField.getAttribute("type");
    }



    public boolean isErrorMessageVisibleO() {
        return errorMessageBox.is(Condition.visible);
    }


    public boolean isErrorMessageClosed() {
        return !errorText.exists();
    }

    public SelenideElement getErrorText() {
        return errorText;
    }

    public SelenideElement getErrorTextElement() {
        return errorText;
    }

    public String getErrorMessageTextX() {
        return errorText.shouldBe(Condition.visible).getText();
    }

    public void closeErrorMessageX() {
        errorCloseBtn.shouldBe(Condition.visible).click();
    }

    public boolean isErrorMessageContainerEmpty() {
        // After closing, the container should still exist but be empty
        return errorMessageBox.exists() && errorMessageBox.getText().isEmpty();
    }

    public void loginExpectingError(String username, String password) {
        usernameField.setValue(username);
        passwordField.setValue(password);
        loginButton.click();
        errorMessageBox.shouldBe(Condition.visible);
    }
    public ProductsPage submitValidLogin() {
        loginButton.shouldBe(Condition.visible).click();
        return new ProductsPage();
    }

    public boolean isLoginButtonDisplayed() {
        return loginButton.isDisplayed();
    }
}
