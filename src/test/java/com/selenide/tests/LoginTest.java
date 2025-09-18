package com.selenide.tests;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.selenide.base.BaseTest;
import com.selenide.listener.JUnitlistener;
import com.selenide.pages.LoginPage;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.File;
import java.io.IOException;

import static com.codeborne.selenide.Selenide.$;
import static org.junit.jupiter.api.Assertions.*;

@Epic("Login Feature")
@ExtendWith(JUnitlistener.class)
@Feature("User Login")
@Tag("smoke")
public class LoginTest extends BaseTest {

    private static JsonNode loginData;
    private LoginPage loginPage;

    @BeforeAll
    public static void loadTestData() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        loginData = mapper.readTree(new File("src/test/resources/testdata/loginData.json"));
    }

    @BeforeEach
    public void setupPage() {
        loginPage = new LoginPage();
        loginPage.openPage();
    }

    @Test
    @Story("Valid user should login successfully")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Verify that a standard user can login with valid credentials")
    public void testValidUserLogin() {
        JsonNode user = loginData.get("validUser");
        loginPage.login(user.get("username").asText(), user.get("password").asText());
        assertTrue(loginPage.isDashboardVisible(), "Dashboard should be visible after successful login");
    }

    @Test
    @Story("Locked out user cannot login")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that a locked out user sees an error message")
    public void testLockedOutUser() {
        JsonNode user = loginData.get("lockedOutUser");
        loginPage.loginExpectingError(user.get("username").asText(), user.get("password").asText());
        assertEquals("Epic sadface: Sorry, this user has been locked out.", loginPage.getErrorMessageText());
    }

    @Test
    @Story("User enters invalid password")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify login fails with invalid password")
    public void testInvalidPassword() {
        JsonNode user = loginData.get("invalidPassword");
        loginPage.loginExpectingError(user.get("username").asText(), user.get("password").asText());
        assertEquals(
                "Epic sadface: Username and password do not match any user in this service",
                loginPage.getErrorMessageText()
        );
    }


    @Test
    @Story("User enters invalid username")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify login fails with invalid username")
    public void testInvalidUsername() {
        JsonNode user = loginData.get("invalidUsername");

        loginPage.loginExpectingError(user.get("username").asText(), user.get("password").asText());

        assertTrue(loginPage.isErrorMessageVisible(), "Error message should be visible for invalid username");
    }


    @Test
    @Story("User leaves username empty")
    @Severity(SeverityLevel.MINOR)
    @Description("Verify login fails if username is empty")
    public void testEmptyUsername() {
        JsonNode user = loginData.get("emptyUsername");

        loginPage.loginExpectingError(user.get("username").asText(), user.get("password").asText());

        assertEquals("Epic sadface: Username is required", loginPage.getErrorMessageText());
    }

    @Test
    @Story("User leaves password empty")
    @Severity(SeverityLevel.MINOR)
    @Description("Verify login fails if password is empty")
    public void testEmptyPassword() {
        JsonNode user = loginData.get("emptyPassword");

        loginPage.loginExpectingError(user.get("username").asText(), user.get("password").asText());

        assertEquals("Epic sadface: Password is required", loginPage.getErrorMessageText());
    }


    @Test
    @Story("User leaves both username and password empty")
    @Severity(SeverityLevel.MINOR)
    @Description("Verify login fails if both fields are empty")
    public void testEmptyBothFields() {
        JsonNode user = loginData.get("emptyBoth");

        loginPage.loginExpectingError(user.get("username").asText(), user.get("password").asText());

        assertEquals("Epic sadface: Username is required", loginPage.getErrorMessageText());
    }


    @Test
    @DisplayName("User can close the error message after failed login")
    @Severity(SeverityLevel.TRIVIAL)
    @Description("Verify that user can close the error message and the container becomes empty")
    public void testCloseErrorMessage() {
        JsonNode user = loginData.get("errorMessageCloseUser");

        // Act: attempt login with invalid credentials
        loginPage.setUsername(user.get("username").asText())
                .setPassword(user.get("password").asText())
                .clickLogin();

        // Assert: error message is visible
        assertTrue(loginPage.getErrorTextElement().is(Condition.visible),
                "Error message should be visible after invalid login");

        // Act: close the error message
        loginPage.closeErrorMessageX();

        // Assert: container is now empty
        assertTrue(loginPage.isErrorMessageContainerEmpty(),
                "Error message container should be empty after closing the error");
    }

    @Test
    @Story("Login button enabled with valid input")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify login button is enabled when valid credentials are entered")
    public void testLoginButtonEnabled() {
        JsonNode user = loginData.get("loginButtonEnabledUser");
        loginPage.enterUsername(user.get("username").asText());
        loginPage.enterPassword(user.get("password").asText());
        assertTrue(loginPage.isLoginButtonEnabled(), "Login button should be enabled");
    }

    @Test
    @Story("Login button disabled with empty fields")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify login button is disabled when username or password is empty")
    public void testLoginButtonDisabled() {
        JsonNode users = loginData.get("loginButtonDisabledUser");
        for (JsonNode user : users) {
            loginPage.enterUsername(user.get("username").asText());
            loginPage.enterPassword(user.get("password").asText());
            assertFalse(loginPage.isLoginButtonEnabled(), "Login button should be disabled for empty fields");
        }
    }
}
