package com.selenide.listener;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;

import java.util.Optional;

/**
 * JUnitListener: listens to test results and captures screenshots on failure.
 */
public class JUnitlistener implements TestWatcher {

    private String getSafeTestName(ExtensionContext context) {
        String className = context.getTestClass().map(Class::getSimpleName).orElse("UnknownClass");
        String methodName = context.getTestMethod().map(m -> m.getName()).orElse("UnknownMethod");
        String unique = context.getUniqueId() == null ? "uid_unknown" :
                context.getUniqueId().replaceAll("[^a-zA-Z0-9._-]", "_");

        return (className + "_" + methodName + "_" + unique).replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    @Override
    public void testSuccessful(ExtensionContext context) {
        String testName = getSafeTestName(context);
        System.out.println("[JUnitListener] PASSED -> " + testName);
        TestListener.onTestSuccess(testName);
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        String testName = getSafeTestName(context);
        System.err.println("[JUnitListener] FAILED -> " + testName);
        // Take screenshot immediately while WebDriver is alive
        TestListener.onTestFailure(testName, cause);
    }

    @Override
    public void testDisabled(ExtensionContext context, Optional<String> reason) {
        String testName = getSafeTestName(context);
        System.out.println("[JUnitListener] SKIPPED -> " + testName + " Reason: " + reason.orElse("No reason"));
        TestListener.onTestSkipped(testName, reason.orElse("No reason"));
    }

    @Override
    public void testAborted(ExtensionContext context, Throwable cause) {
        String testName = getSafeTestName(context);
        System.out.println("[JUnitListener] ABORTED -> " + testName);
        TestListener.onTestFailure(testName, cause);
    }
}
