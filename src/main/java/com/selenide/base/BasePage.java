package com.selenide.base;


import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.codeborne.selenide.Selenide.$;


// BasePage - common reusable methods for all page objects.

public abstract class BasePage {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    // every page should define its own unique identifier
    protected abstract SelenideElement getPageIdentifier();


     // Verify that the page is loaded by checking its unique identifier.

    @Step("Verify page is loaded")
    public boolean isPageLoaded() {
        try {
            getPageIdentifier().shouldBe(Condition.visible);
            logger.info(" Page [{}] loaded successfully.", this.getClass().getSimpleName());
            return true;
        } catch (Exception e) {
            logger.error(" Page [{}] failed to load.", this.getClass().getSimpleName(), e);
            return false;
        }
    }


     // Click on an element.

    @Step("Click element: {element}")
    protected void click(SelenideElement element) {
        element.shouldBe(Condition.visible).click();
        logger.info("Clicked element: {}", element);
    }


      // Type text into an input field.

    @Step("Type '{text}' into element: {element}")
    protected void type(SelenideElement element, String text) {
        element.shouldBe(Condition.visible).setValue(text);
        logger.info("Typed '{}' into element: {}", text, element);
    }


     // Get text from an element.

    @Step("Get text from element: {element}")
    protected String getText(SelenideElement element) {
        String text = element.shouldBe(Condition.visible).getText();
        logger.info("Got text '{}' from element: {}", text, element);
        return text;
    }


     //  Check if the element is visible.

    @Step("Check if element is visible: {element}")
    protected boolean isVisible(SelenideElement element) {
        boolean visible = element.is(Condition.visible);
        logger.info("Element {} visible: {}", element, visible);
        return visible;
    }


     // Wait until the element is visible.

    @Step("Wait for element to be visible: {element}")
    protected void waitForVisibility(SelenideElement element) {
        element.shouldBe(Condition.visible);
        logger.info("Waited until element {} is visible", element);
    }
}
