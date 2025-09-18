package com.selenide.tests;



import com.codeborne.selenide.WebDriverRunner;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.selenide.base.BaseTest;
import com.selenide.listener.JUnitlistener;
import com.selenide.pages.CartPage;
import com.selenide.pages.CheckoutPage;
import com.selenide.pages.LoginPage;
import com.selenide.pages.ProductsPage;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Epic("Checkout Feature")
@ExtendWith(JUnitlistener.class)
@Feature("Checkout Page")
@Tag("regression")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CheckoutTests extends BaseTest {

    private static JsonNode checkoutData;
    private static JsonNode validUser;
    private static String expectedOverviewTitle;

    private ProductsPage productsPage;
    private CartPage cartPage;
    private CheckoutPage checkoutPage;

    @BeforeAll
    public static void loadTestData() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        checkoutData = mapper.readTree(new File("src/test/resources/testdata/checkoutData.json"));
        validUser = checkoutData.get("validUser");
        expectedOverviewTitle = checkoutData.get("overviewTitle").asText();
    }

    @BeforeEach
    public void loginAndNavigateToCheckoutPage() {
        // Login → ProductsPage
        productsPage = new LoginPage()
                .openPage()
                .setUsername(validUser.get("username").asText())
                .setPassword(validUser.get("password").asText())
                .submitValidLogin();

        // Add at least one product to cart before navigating to CartPage
        String productName = checkoutData.get("testProduct").asText();
        productsPage.addProductToCart(productName);

        // Navigate: ProductsPage → CartPage → CheckoutPage
        cartPage = productsPage.openCart();
        checkoutPage = cartPage.checkout(); // CartPage.checkout() returns CheckoutPage
    }


    @Test
    @Story("Checkout Information Page")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that the Checkout Information page title is displayed correctly")
    @DisplayName("Checkout Information page title test")
    public void testCheckoutInformationPageTitle() {
        String expectedTitle = checkoutData.get("informationTitle").asText();
        String actualTitle = checkoutPage.getInformationTitle();
        assertEquals(expectedTitle, actualTitle,
                "Checkout Information page title should match expected value");
    }


    @Test
    @Story("Checkout Information Validation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify error when all fields are empty")
    @DisplayName("Checkout empty fields validation")
    public void testEmptyAllFields() {
        JsonNode testCase = checkoutData.get("emptyFields").get(0);
        checkoutPage.enterFirstName(testCase.get("firstName").asText())
                .enterLastName(testCase.get("lastName").asText())
                .enterPostalCode(testCase.get("postalCode").asText())
                .continueCheckout();

        assertEquals(testCase.get("expectedError").asText(), checkoutPage.getErrorMessage());
        checkoutPage.closeErrorMessage();
    }

    @Test
    @Story("Checkout Information Validation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify error when first name is empty")
    @DisplayName("Checkout empty first name validation")
    public void testEmptyFirstName() {
        JsonNode testCase = checkoutData.get("emptyFields").get(1);
        checkoutPage.enterFirstName(testCase.get("firstName").asText())
                .enterLastName(testCase.get("lastName").asText())
                .enterPostalCode(testCase.get("postalCode").asText())
                .continueCheckout();

        assertEquals(testCase.get("expectedError").asText(), checkoutPage.getErrorMessage());
        checkoutPage.closeErrorMessage();
    }

    @Test
    @Story("Checkout Information Validation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify error when last name is empty")
    @DisplayName("Checkout empty last name validation")
    public void testEmptyLastName() {
        JsonNode testCase = checkoutData.get("emptyFields").get(2);
        checkoutPage.enterFirstName(testCase.get("firstName").asText())
                .enterLastName(testCase.get("lastName").asText())
                .enterPostalCode(testCase.get("postalCode").asText())
                .continueCheckout();

        assertEquals(testCase.get("expectedError").asText(), checkoutPage.getErrorMessage());
        checkoutPage.closeErrorMessage();
    }

    @Test
    @Story("Checkout Information Validation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify error when postal code is empty")
    @DisplayName("Checkout empty postal code validation")
    public void testEmptyPostalCode() {
        JsonNode testCase = checkoutData.get("emptyFields").get(3);
        checkoutPage.enterFirstName(testCase.get("firstName").asText())
                .enterLastName(testCase.get("lastName").asText())
                .enterPostalCode(testCase.get("postalCode").asText())
                .continueCheckout();

        assertEquals(testCase.get("expectedError").asText(), checkoutPage.getErrorMessage());
        checkoutPage.closeErrorMessage();
    }

    @Test
    @Story("Checkout Information Validation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify error when last name and postal code are empty")
    @DisplayName("Checkout empty last name and postal code validation")
    public void testEmptyLastNameAndPostalCode() {
        JsonNode testCase = checkoutData.get("emptyFields").get(4);
        checkoutPage.enterFirstName(testCase.get("firstName").asText())
                .enterLastName(testCase.get("lastName").asText())
                .enterPostalCode(testCase.get("postalCode").asText())
                .continueCheckout();

        assertEquals(testCase.get("expectedError").asText(), checkoutPage.getErrorMessage());
        checkoutPage.closeErrorMessage();
    }

    @Test
    @Story("Checkout Information Validation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify error when first name and last name contain only numeric values")
    @DisplayName("Checkout numeric names validation")
    public void testNumericFirstAndLastName() {
        JsonNode testCase = checkoutData.get("numericNameFields").get(0);

        checkoutPage.enterFirstName(testCase.get("firstName").asText())
                .enterLastName(testCase.get("lastName").asText())
                .enterPostalCode(testCase.get("postalCode").asText())
                .continueCheckout();

        String actualError = checkoutPage.getErrorMessage();
        assertEquals(testCase.get("expectedError").asText(), actualError,
                "Error message should match expected for numeric first and last name");

        checkoutPage.closeErrorMessage();
    }


    @Test
    @Story("Checkout Information Validation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify error when first name contains only numeric values")
    @DisplayName("Checkout numeric first name validation")
    public void testNumericFirstNameOnly() {
        JsonNode testCase = checkoutData.get("numericNameFields").get(1); // second entry

        checkoutPage.enterFirstName(testCase.get("firstName").asText())
                .enterLastName(testCase.get("lastName").asText())
                .enterPostalCode(testCase.get("postalCode").asText())
                .continueCheckout();

        String actualError = checkoutPage.getErrorMessage();
        assertEquals(testCase.get("expectedError").asText(), actualError,
                "Error message should match expected for numeric first name");

        checkoutPage.closeErrorMessage();
    }

    @Test
    @Story("Checkout Information Validation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify error when last name contains only numeric values")
    @DisplayName("Checkout numeric last name validation")
    public void testNumericLastNameOnly() {
        JsonNode testCase = checkoutData.get("numericNameFields").get(2); // third entry

        checkoutPage.enterFirstName(testCase.get("firstName").asText())
                .enterLastName(testCase.get("lastName").asText())
                .enterPostalCode(testCase.get("postalCode").asText())
                .continueCheckout();

        String actualError = checkoutPage.getErrorMessage();
        assertEquals(testCase.get("expectedError").asText(), actualError,
                "Error message should match expected for numeric last name");

        checkoutPage.closeErrorMessage();
    }

    @Test
    @Story("Checkout Information Validation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify error when first name, last name, and postal code contain special characters")
    @DisplayName("Checkout - All Fields Special Characters")
    public void testSpecialCharactersAllFields() {
        JsonNode testCase = checkoutData.get("specialCharFields").get(0);

        checkoutPage.enterFirstName(testCase.get("firstName").asText())
                .enterLastName(testCase.get("lastName").asText())
                .enterPostalCode(testCase.get("postalCode").asText())
                .continueCheckout();

        String actualError = checkoutPage.getErrorMessage();
        assertEquals(testCase.get("expectedError").asText(), actualError);
        checkoutPage.closeErrorMessage();
    }

    @Test
    @Story("Checkout Information Validation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify error when only postal code contains special characters")
    @DisplayName("Checkout - Postal Code Special Characters")
    public void testSpecialCharactersPostalCode() {
        JsonNode testCase = checkoutData.get("specialCharFields").get(1);

        checkoutPage.enterFirstName(testCase.get("firstName").asText())
                .enterLastName(testCase.get("lastName").asText())
                .enterPostalCode(testCase.get("postalCode").asText())
                .continueCheckout();

        String actualError = checkoutPage.getErrorMessage();
        assertEquals(testCase.get("expectedError").asText(), actualError);
        checkoutPage.closeErrorMessage();
    }

    @Test
    @Story("Checkout Information Validation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify error when first name contains special characters")
    @DisplayName("Checkout - First Name Special Characters")
    public void testSpecialCharactersFirstName() {
        JsonNode testCase = checkoutData.get("specialCharFields").get(2);

        checkoutPage.enterFirstName(testCase.get("firstName").asText())
                .enterLastName(testCase.get("lastName").asText())
                .enterPostalCode(testCase.get("postalCode").asText())
                .continueCheckout();

        String actualError = checkoutPage.getErrorMessage();
        assertEquals(testCase.get("expectedError").asText(), actualError);
        checkoutPage.closeErrorMessage();
    }

    @Test
    @Story("Checkout Information Validation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify error when last name contains special characters")
    @DisplayName("Checkout - Last Name Special Characters")
    public void testSpecialCharactersLastName() {
        JsonNode testCase = checkoutData.get("specialCharFields").get(3);

        checkoutPage.enterFirstName(testCase.get("firstName").asText())
                .enterLastName(testCase.get("lastName").asText())
                .enterPostalCode(testCase.get("postalCode").asText())
                .continueCheckout();

        String actualError = checkoutPage.getErrorMessage();
        assertEquals(testCase.get("expectedError").asText(), actualError);
        checkoutPage.closeErrorMessage();
    }

    @Test
    @Story("Checkout Cancel Functionality")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that clicking Cancel on Checkout Overview page returns user to homepage")
    @DisplayName("Checkout Cancel returns to Homepage")
    public void testCheckoutCancelReturnsHome() {
        // Fill valid information from JSON
        checkoutPage.enterFirstName(checkoutData.get("validInfo").get("firstName").asText())
                .enterLastName(checkoutData.get("validInfo").get("lastName").asText())
                .enterPostalCode(checkoutData.get("validInfo").get("postalCode").asText())
                .continueCheckout(); // proceed to Overview page

        // Scroll and click Cancel
        checkoutPage.cancelCheckout();

        // Verify redirect to homepage URL from JSON
        String expectedHomeUrl = checkoutData.get("urls").get("homePage").asText();
        String currentUrl = productsPage.getCurrentUrl();
        assertEquals(expectedHomeUrl, currentUrl,
                "Clicking Cancel should return user to the homepage");
    }


    @Test
    @Story("Checkout Completion")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that clicking Finish on Checkout Overview page shows success message")
    @DisplayName("Checkout Finish shows success message")
    public void testCheckoutFinishShowsSuccessMessage() {
        // Fill valid information from JSON and continue to overview
        checkoutPage.enterFirstName(checkoutData.get("validInfo").get("firstName").asText())
                .enterLastName(checkoutData.get("validInfo").get("lastName").asText())
                .enterPostalCode(checkoutData.get("validInfo").get("postalCode").asText())
                .continueCheckout(); // proceed to Overview page

        // Scroll and click Finish
        checkoutPage.finishCheckout();

        // Load expected success message from checkoutData.json
        String expectedMessage = checkoutData.get("successMessage").asText();

        // Assert success message
        String actualMessage = checkoutPage.getSuccessMessage();
        assertEquals(expectedMessage, actualMessage,
                "Checkout completion should display success message");
    }

    @Test
    @Story("Finish checkout and navigate back to products page")
    @Severity(SeverityLevel.CRITICAL)
    @Description("This test fills checkout form with valid data, " +
            "finishes checkout, verifies success message from JSON, " +
            "and navigates back to the products page.")
    @DisplayName("Checkout Finish and Navigate Back Home")
    public void testCheckoutFinishAndBackHome() {
        // Fill valid information from JSON and continue to overview
        checkoutPage.enterFirstName(checkoutData.get("validInfo").get("firstName").asText())
                .enterLastName(checkoutData.get("validInfo").get("lastName").asText())
                .enterPostalCode(checkoutData.get("validInfo").get("postalCode").asText())
                .continueCheckout(); // proceed to Overview page

        // Scroll and click Finish
        checkoutPage.finishCheckout();

        // Assert success message from JSON
        String expectedMessage = checkoutData.get("successMessage").asText();
        String actualMessage = checkoutPage.getSuccessMessage();
        assertEquals(expectedMessage, actualMessage,
                "Checkout completion should display success message");

        // Click Back Home to return to Products page
        checkoutPage.backToHome();

        // Assert that we are back on Products page
        String currentUrl = WebDriverRunner.url();
        assertTrue(currentUrl.contains("/inventory.html"), "Should navigate back to Products page");
    }

}
