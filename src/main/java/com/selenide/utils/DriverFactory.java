package com.selenide.utils;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;

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
                Configuration.browser = "chrome";

                ChromeOptions options = new ChromeOptions();

                // Always required for Docker
                options.addArguments("--no-sandbox");
                options.addArguments("--disable-dev-shm-usage");
                options.addArguments("--disable-gpu");
                options.addArguments("--disable-extensions");
                options.addArguments("--disable-infobars");
                options.addArguments("--remote-allow-origins=*");

                if (headless) {
                    options.addArguments("--headless=new");
                    options.addArguments("--window-size=1920,1080");
                } else {
                    options.addArguments("--start-maximized");
                }

                // Avoid Chrome user data dir conflicts
                options.addArguments("--user-data-dir=/tmp/chrome-" + System.currentTimeMillis());

                Configuration.browserCapabilities = options;
                break;

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

        if (headless) {
            Configuration.browserSize = "1920x1080";
        } else {
            Configuration.browserSize = null;
        }

        // ⚠️ Removed auto-open here
        if (!WebDriverRunner.hasWebDriverStarted()) {
            // Do NOT open URL here. Let tests decide when to open.
        }
    }

    public static void quitDriver() {
        if (WebDriverRunner.hasWebDriverStarted()) {
            WebDriverRunner.closeWebDriver();
        }
    }
}
