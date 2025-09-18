package com.selenide.utils;


import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.executeJavaScript;

/**
 * WaitUtil - small utility wrapper around common wait patterns.
 * Uses Selenide's Condition waits where possible and Selenium's WebDriverWait for URL/JS conditions.
 */
public final class WaitUtil {

    private WaitUtil() { /* utility class */ }

    private static Duration durationOrDefault(Integer secondsOverride) {
        int defaultSeconds;
        try {
            defaultSeconds = ConfigReader.getInt("timeout");
        } catch (Exception e) {
            defaultSeconds = 10; // fallback
        }
        int seconds = secondsOverride == null ? defaultSeconds : secondsOverride;
        return Duration.ofSeconds(seconds);
    }

    // -------------------------
    // Element visibility/clickability
    // -------------------------

    /** Waits until element is visible and returns it (throws RuntimeException on timeout). */
    public static SelenideElement waitForVisibility(SelenideElement element) {
        return waitForVisibility(element, null);
    }

    public static SelenideElement waitForVisibility(SelenideElement element, Integer timeoutSeconds) {
        try {
            element.shouldBe(Condition.visible, durationOrDefault(timeoutSeconds));
            return element;
        } catch (AssertionError e) {
            throw new RuntimeException("Element was not visible within timeout", e);
        }
    }

    /** Waits until element is visible and enabled (clickable) and returns it. */
    public static SelenideElement waitForClickable(SelenideElement element) {
        return waitForClickable(element, null);
    }

    public static SelenideElement waitForClickable(SelenideElement element, Integer timeoutSeconds) {
        Duration d = durationOrDefault(timeoutSeconds);
        try {
            element.shouldBe(Condition.visible, d);
            element.shouldBe(Condition.enabled, d);
            return element;
        } catch (AssertionError e) {
            throw new RuntimeException("Element was not clickable within timeout", e);
        }
    }

    // -------------------------
    // Text / attribute / css waits
    // -------------------------

    /** Waits until element contains the specified text. Returns true if successful. */
    public static boolean waitForText(SelenideElement element, String text) {
        return waitForText(element, text, null);
    }

    public static boolean waitForText(SelenideElement element, String text, Integer timeoutSeconds) {
        try {
            element.shouldHave(Condition.text(text), durationOrDefault(timeoutSeconds));
            return true;
        } catch (AssertionError e) {
            return false;
        }
    }

    /** Waits until element has the given attribute value. */
    public static boolean waitForAttribute(SelenideElement element, String attribute, String value) {
        return waitForAttribute(element, attribute, value, null);
    }

    public static boolean waitForAttribute(SelenideElement element, String attribute, String value, Integer timeoutSeconds) {
        try {
            element.shouldHave(Condition.attribute(attribute, value), durationOrDefault(timeoutSeconds));
            return true;
        } catch (AssertionError e) {
            return false;
        }
    }

    /** Waits until element has the specified CSS value. */
    public static boolean waitForCssValue(SelenideElement element, String cssProperty, String expectedValue) {
        return waitForCssValue(element, cssProperty, expectedValue, null);
    }

    public static boolean waitForCssValue(SelenideElement element, String cssProperty, String expectedValue, Integer timeoutSeconds) {
        try {
            element.shouldHave(Condition.cssValue(cssProperty, expectedValue), durationOrDefault(timeoutSeconds));
            return true;
        } catch (AssertionError e) {
            return false;
        }
    }

    // -------------------------
    // Collection / count waits
    // -------------------------

    /**
     * Waits until the number of elements located by 'locator' equals expectedCount.
     * Returns true if matched within timeout.
     */
    public static boolean waitForNumberOfElements(By locator, int expectedCount) {
        return waitForNumberOfElements(locator, expectedCount, null);
    }

    public static boolean waitForNumberOfElements(By locator, int expectedCount, Integer timeoutSeconds) {
        try {
            $$(locator).shouldHave(CollectionCondition.size(expectedCount), durationOrDefault(timeoutSeconds));
            return true;
        } catch (AssertionError e) {
            return false;
        }
    }

    /**
     * Waits until there's at least one element matching locator.
     */
    public static boolean waitForAtLeastOne(By locator) {
        return waitForAtLeastOne(locator, null);
    }

    public static boolean waitForAtLeastOne(By locator, Integer timeoutSeconds) {
        try {
            $$(locator).shouldHave(CollectionCondition.sizeGreaterThan(0), durationOrDefault(timeoutSeconds));
            return true;
        } catch (AssertionError e) {
            return false;
        }
    }

    // -------------------------
    // Disappear / invisibility
    // -------------------------

    /** Waits until the element disappears (is not present or not visible). */
    public static boolean waitForElementToDisappear(SelenideElement element) {
        return waitForElementToDisappear(element, null);
    }

    public static boolean waitForElementToDisappear(SelenideElement element, Integer timeoutSeconds) {
        try {
            element.should(Condition.disappear, durationOrDefault(timeoutSeconds));
            return true;
        } catch (AssertionError e) {
            return false;
        }
    }

    // -------------------------
    // URL / JS / AJAX waits
    // -------------------------

    /**
     * Waits until the current URL contains the provided substring.
     * Returns true if condition met within timeout.
     */
    public static boolean waitForUrlContains(String substring) {
        return waitForUrlContains(substring, null);
    }

    public static boolean waitForUrlContains(String substring, Integer timeoutSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(WebDriverRunner.getWebDriver(), durationOrDefault(timeoutSeconds));
            return wait.until((ExpectedCondition<Boolean>) driver -> driver.getCurrentUrl().contains(substring));
        } catch (TimeoutException e) {
            return false;
        }
    }

    /**
     * Waits for document.readyState === 'complete' and (if jQuery present) jQuery.active === 0.
     * Useful for AJAX-heavy pages.
     */
    public static boolean waitForAjaxComplete() {
        return waitForAjaxComplete(null);
    }

    public static boolean waitForAjaxComplete(Integer timeoutSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(WebDriverRunner.getWebDriver(), durationOrDefault(timeoutSeconds));
            return wait.until(driver -> {
                Object readyState = ((JavascriptExecutor) driver).executeScript("return document.readyState");
                boolean ready = "complete".equals(readyState);

                Object jqueryActive = null;
                try {
                    jqueryActive = ((JavascriptExecutor) driver)
                            .executeScript("return (window.jQuery == undefined) ? 0 : window.jQuery.active");
                } catch (Exception ignored) { /* ignore if jQuery not present */ }

                boolean jqueryOk = jqueryActive == null || Long.valueOf(0).equals(((Number) jqueryActive).longValue());
                return ready && jqueryOk;
            });
        } catch (TimeoutException e) {
            return false;
        }
    }

    // -------------------------
    // Misc helpers
    // -------------------------

    /** Convenience: wait for element located by locator to be visible and return it. */
    public static SelenideElement waitForVisibility(By locator) {
        return waitForVisibility(locator, null);
    }

    public static SelenideElement waitForVisibility(By locator, Integer timeoutSeconds) {
        SelenideElement el = $$(locator).first();
        return waitForVisibility(el, timeoutSeconds);
    }
}
