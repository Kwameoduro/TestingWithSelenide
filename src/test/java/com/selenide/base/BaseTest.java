package com.selenide.base;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import com.selenide.listener.TestListener;
import com.selenide.utils.ConfigReader;
import com.selenide.utils.DriverFactory;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * BaseTest - common setup & teardown for all tests.
 * Integrates TestListener manually for logging, screenshots, and page source.
 */
public abstract class BaseTest {

    protected static final Logger logger = LoggerFactory.getLogger(BaseTest.class);
    protected String currentTestName;

    @BeforeAll
    public static void globalSetup() {
        logger.info("=== Test Suite Initialization Started ===");

        // Read configuration
        String baseUrl = ConfigReader.get("base.url", "https://www.saucedemo.com/");
        String browser = ConfigReader.get("browser", "chrome");
        boolean headless = Boolean.parseBoolean(ConfigReader.get("headless", "false"));

        // Configure Selenide
        Configuration.baseUrl = baseUrl;
        Configuration.browser = browser;
        Configuration.headless = headless;
        Configuration.timeout = 5000;
        Configuration.screenshots = true;
        Configuration.savePageSource = true;

        logger.info("Base URL: {}", baseUrl);
        logger.info("Browser: {} | Headless: {}", browser, headless);
        logger.info("=== Test Suite Initialization Complete ===");
    }

    @BeforeEach
    public void setUp(TestInfo testInfo) {
        currentTestName = testInfo.getDisplayName();
        logger.info("=== Starting Test: {} ===", currentTestName);

        // Initialize WebDriver
        DriverFactory.initDriver();

        // Notify listener manually
        TestListener.onTestStart(currentTestName);
    }

    @AfterEach
    public void tearDown(TestInfo testInfo) {
        logger.info("=== Cleaning up Test: {} ===", currentTestName);

        if (WebDriverRunner.hasWebDriverStarted()) {
            DriverFactory.quitDriver();
        }

        // Notify listener manually (success/failure handling can be improved if needed)
        TestListener.onTestSuccess(currentTestName);
    }

    @AfterAll
    public static void globalTeardown() {
        logger.info("=== Test Suite Execution Finished ===");
    }
}
