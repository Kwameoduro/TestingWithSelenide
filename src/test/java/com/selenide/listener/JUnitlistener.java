package com.selenide.listener;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;

import java.util.Optional;

public class JUnitlistener implements TestWatcher {

    @Override
    public void testSuccessful(ExtensionContext context) {
        TestListener.onTestSuccess(context.getDisplayName());
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        TestListener.onTestFailure(context.getDisplayName(), cause);
    }

    @Override
    public void testDisabled(ExtensionContext context, Optional<String> reason) {
        TestListener.onTestSkipped(context.getDisplayName(), reason.orElse("No reason"));
    }

    @Override
    public void testAborted(ExtensionContext context, Throwable cause) {
        TestListener.onTestFailure(context.getDisplayName(), cause);
    }
}
