package com.selenide.utils;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import org.openqa.selenium.WebDriver;

public class DriverFactory {

    private DriverFactory() {}

    public static void initDriver() {
        String browser = ConfigReader.get("browser", "chrome").toLowerCase();
        boolean headless = ConfigReader.getBoolean("headless", false);
        int timeout = ConfigReader.getInt("timeout", 10);
        String baseUrl = ConfigReader.get("base.url", "https://www.saucedemo.com/")
                .replaceAll("/+$", "");

        // Browser setup
        switch (browser) {
            case "chrome":
            case "firefox":
            case "edge":
                Configuration.browser = browser;
                break;
            default:
                System.out.println(" Unsupported browser: " + browser + " → Falling back to Chrome.");
                Configuration.browser = "chrome";
        }

        // Global Selenide settings
        Configuration.baseUrl = baseUrl;
        Configuration.headless = headless;
        Configuration.timeout = timeout * 1000L;
        Configuration.screenshots = true;
        Configuration.savePageSource = true;
        Configuration.reportsFolder = "target/allure-results";

        // Browser window handling
        if (headless) {
            // headless → fixed size
            Configuration.browserSize = "1920x1080";
        } else {
            // local run → maximize later
            Configuration.browserSize = null;
        }

        // Start browser only if not started already
        if (!WebDriverRunner.hasWebDriverStarted()) {
            Selenide.open("/"); // relative URL, uses baseUrl

            if (!headless) {
                WebDriver driver = WebDriverRunner.getWebDriver();
                driver.manage().window().maximize();
            }
        }
    }

    public static void quitDriver() {
        if (WebDriverRunner.hasWebDriverStarted()) {
            WebDriverRunner.closeWebDriver();
        }
    }
}
