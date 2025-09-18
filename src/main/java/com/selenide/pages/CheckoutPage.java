package com.selenide.pages;



import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;


public class CheckoutPage {

    private final SelenideElement infoTitle = $(".title"); // already same selector as overview
    private final SelenideElement errorMessage = $(".error-message-container");
    private final SelenideElement errorCloseButton = $(".error-button");
    private final SelenideElement cancelBtn = $("button[data-test='cancel']");
    private final SelenideElement finishBtn = $("button[data-test='finish']");



    // Step One: User Information
    private final SelenideElement firstNameInput = $("#first-name");
    private final SelenideElement lastNameInput = $("#last-name");
    private final SelenideElement postalCodeInput = $("#postal-code");
    private final SelenideElement continueButton = $("#continue");
    private final SelenideElement cancelButton = $("#cancel");

    // Step Two: Overview
    private final SelenideElement finishButton = $("#finish");
    private final SelenideElement overviewTitle = $(".title");

    // Completion
    private final SelenideElement completeHeader = $(".complete-header");
    private final SelenideElement backHomeButton = $("#back-to-products");

    /**
     * Enter checkout information
     */
    public CheckoutPage enterFirstName(String firstName) {
        firstNameInput.setValue(firstName);
        return this;
    }

    public CheckoutPage enterLastName(String lastName) {
        lastNameInput.setValue(lastName);
        return this;
    }

    public CheckoutPage enterPostalCode(String postalCode) {
        postalCodeInput.setValue(postalCode);
        return this;
    }

    /**
     * Proceed to next step
     */
    public CheckoutPage continueCheckout() {
        continueButton.click();
        return this;
    }


    public String getOverviewTitle() {
        return overviewTitle.getText();
    }

    /**
     * Completion
     */
    public String getCompletionMessage() {
        return completeHeader.getText();
    }

    public ProductsPage backToHome() {
        backHomeButton.click();
        return new ProductsPage();
    }

    public String getInformationTitle() {
        return infoTitle.getText();
    }


    public String getErrorMessage() {
        return errorMessage.getText();
    }

    public CheckoutPage closeErrorMessage() {
        errorCloseButton.click();
        return this;
    }

    public CheckoutPage cancelCheckout() {
        // Scroll to the Cancel button before clicking
        cancelBtn.scrollIntoView(true).click();
        return this;
    }

    public CheckoutPage finishCheckout() {
        // Scroll to the Finish button before clicking
        finishBtn.scrollIntoView(true).click();
        return this;
    }

    public String getSuccessMessage() {
        return $("h2.complete-header").shouldBe(visible).getText();
    }



}
