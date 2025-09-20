package com.selenide.base;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import com.selenide.listener.JUnitlistener;
import com.selenide.utils.ConfigReader;
import com.selenide.utils.DriverFactory;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * BaseTest: complete setup and teardown for all tests.
 * Guarantees WebDriver per test and integrates JUnitListener for screenshots.
 */
public abstract class BaseTest {

    protected static final Logger logger = LoggerFactory.getLogger(BaseTest.class);

    // Register JUnitListener extension to capture test results
    @RegisterExtension
    public static JUnitlistener JUNIT_LISTENER = new JUnitlistener();

    @BeforeAll
    public static void globalSetup() {
        logger.info("=== Test Suite Initialization Started ===");

        String baseUrl = ConfigReader.get("base.url", "https://www.saucedemo.com/");
        String browser = ConfigReader.get("browser", "chrome");
        boolean headless = Boolean.parseBoolean(ConfigReader.get("headless", "false"));

        Configuration.baseUrl = baseUrl;
        Configuration.browser = browser;
        Configuration.headless = headless;
        Configuration.timeout = 5000;
        Configuration.screenshots = false; // we handle screenshots manually
        Configuration.savePageSource = false;
        Configuration.reportsFolder = "target/screenshots";

        logger.info("Base URL: {}", baseUrl);
        logger.info("Browser: {} | Headless: {}", browser, headless);
        logger.info("=== Test Suite Initialization Complete ===");
    }

    @BeforeEach
    public void setUp(TestInfo testInfo) {
        logger.info("=== Starting Test: {} ===", testInfo.getDisplayName());
        DriverFactory.initDriver(); // new driver per test
    }

    @AfterEach
    public void tearDown() {
        // Quit driver after listener has processed failure
        if (WebDriverRunner.hasWebDriverStarted()) {
            DriverFactory.quitDriver();
        }
        logger.info("=== Test Cleaned Up ===");
    }

    @AfterAll
    public static void globalTeardown() {
        logger.info("=== Test Suite Execution Finished ===");
    }
}
