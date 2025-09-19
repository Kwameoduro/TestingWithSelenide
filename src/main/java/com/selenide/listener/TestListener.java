package com.selenide.listener;

import com.codeborne.selenide.Screenshots;
import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.Allure;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;


 // Can be called manually from tests or a base test class.

public class TestListener {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");

    public static void onTestStart(String testName) {
        System.out.println("=== STARTING TEST: " + testName + " ===");
    }

    public static void onTestSuccess(String testName) {
        System.out.println("=== PASSED: " + testName + " ===");
    }

    public static void onTestSkipped(String testName, String reason) {
        System.out.println("=== SKIPPED: " + testName + " - " + (reason != null ? reason : "No reason") + " ===");
    }

    public static void onTestFailure(String testName, Throwable cause) {
        System.err.println("=== FAILED: " + testName + " ===");
        if (cause != null) {
            System.err.println("Cause: " + cause.getMessage());
        }

        if (!WebDriverRunner.hasWebDriverStarted()) {
            System.err.println("WebDriver not started; cannot capture screenshot or page source.");
            return;
        }

        try {
            // Capture screenshot
            File screenshot = Screenshots.takeScreenShotAsFile();
            if (screenshot != null && screenshot.exists()) {
                String screenshotName = "screenshot_" + testName + "_" + DATE_FORMAT.format(new Date()) + ".png";
                Allure.addAttachment(screenshotName, new ByteArrayInputStream(FileUtils.readFileToByteArray(screenshot)));
            } else {
                byte[] bytes = ((TakesScreenshot) WebDriverRunner.getWebDriver()).getScreenshotAs(OutputType.BYTES);
                Allure.addAttachment("screenshot_" + testName, new ByteArrayInputStream(bytes));
            }

            // Capture page source
            String pageSource = WebDriverRunner.getWebDriver().getPageSource();
            Allure.addAttachment("Page Source",
                    "text/html",
                    new ByteArrayInputStream(pageSource.getBytes(StandardCharsets.UTF_8)),
                    ".html");

        } catch (Exception e) {
            System.err.println("Could not capture screenshot/page source: " + e.getMessage());
        }
    }
}
