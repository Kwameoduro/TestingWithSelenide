package com.selenide.listener;

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
import java.util.UUID;


 // TestListener: captures screenshots and page sources for failed tests
 // and attaches them to Allure reports.

public class TestListener {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss_SSS");

    public static void onTestFailure(String testName, Throwable cause) {
        System.err.println("=== FAILED: " + testName + " ===");
        if (cause != null) System.err.println("Cause: " + cause);

        if (!WebDriverRunner.hasWebDriverStarted()) {
            System.err.println("WebDriver not started; cannot capture screenshot.");
            return;
        }

        try {
            String timestamp = DATE_FORMAT.format(new Date());
            String uuid = UUID.randomUUID().toString().substring(0, 8);
            String safeTestName = (testName == null ? "unknown_test" : testName)
                    .replaceAll("[^a-zA-Z0-9._-]", "_");

            String screenshotFileName = safeTestName + "_" + timestamp + "_" + uuid + ".png";
            String pageSourceFileName = safeTestName + "_" + timestamp + "_" + uuid + ".html";

            // Capture screenshot via WebDriver (fresh bytes every time)
            byte[] screenshotBytes = ((TakesScreenshot) WebDriverRunner.getWebDriver())
                    .getScreenshotAs(OutputType.BYTES);

            // Save locally
            File targetDir = new File("target/screenshots");
            targetDir.mkdirs();
            File targetFile = new File(targetDir, screenshotFileName);
            FileUtils.writeByteArrayToFile(targetFile, screenshotBytes);
            System.out.println("[TestListener] Screenshot saved: " + targetFile.getAbsolutePath());

            // Attach to Allure
            Allure.addAttachment(screenshotFileName, new ByteArrayInputStream(screenshotBytes));

            // Attach page source
            String pageSource = WebDriverRunner.getWebDriver().getPageSource();
            Allure.addAttachment("PageSource_" + pageSourceFileName,
                    "text/html",
                    new ByteArrayInputStream(pageSource.getBytes(StandardCharsets.UTF_8)),
                    ".html");

        } catch (Exception e) {
            System.err.println("Failed to capture screenshot/page source: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void onTestSuccess(String testName) {
        System.out.println("=== PASSED: " + testName + " ===");
    }

    public static void onTestSkipped(String testName, String reason) {
        System.out.println("=== SKIPPED: " + testName + " - " + reason + " ===");
    }
}
