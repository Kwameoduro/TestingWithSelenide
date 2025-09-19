package com.selenide.pages;



import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.WebDriverRunner;

import java.util.List;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Condition.text;
import static java.time.zone.ZoneRulesProvider.refresh;

public class ProductsPage {

    // Locators
    private final SelenideElement pageTitle = $(".title");
    private final ElementsCollection inventoryItems = $$(".inventory_item");
    private final SelenideElement cartIcon = $(".shopping_cart_link");
    private final SelenideElement cartBadge = $(".shopping_cart_badge");
    private final SelenideElement sortDropdown = $(".product_sort_container");
    private final ElementsCollection productNames = $$(".inventory_item_name");
    private final SelenideElement menuButton = $("#react-burger-menu-btn");
    private final SelenideElement logoutLink = $("#logout_sidebar_link");


    private SelenideElement removeButton(String productName) {
        return inventoryItems
                .findBy(Condition.text(productName))
                .$("button"); // Button text changes Add → Remove
    }


    // Get the number of items shown in the cart badge

    public int getCartItemCount() {
        return cartBadge.exists() ? Integer.parseInt(cartBadge.getText()) : 0;
    }

     // Verify that we are on the Products page

    public String getPageTitle() {
        return pageTitle.getText();
    }


     // Get number of products listed

    public String getCartBadgeCount() {
        if (cartBadge.exists()) {
            return cartBadge.shouldBe(Condition.visible).getText();
        }
        return "0";
    }


    /**
     * Add a product to the cart by product name
     */
    public ProductsPage addProductToCart(String productName) {
        inventoryItems
                .findBy(text(productName))
                .$("button").click();
        return this;
    }

    /**
     * Navigate to the shopping cart page
     */
    public CartPage goToCart() {
        cartIcon.click();
        return new CartPage();
    }

    public ProductsPage refreshPage() {
        refresh();
        return this;
    }

    public ProductsPage removeProductFromCart(String productName) {
        removeButton(productName).shouldBe(Condition.visible).click();
        return this;
    }

    public boolean isProductInCart(String productName) {
        return removeButton(productName).getText().equalsIgnoreCase("Remove");
    }

    public ProductDetailPage openProductDetail(String productName) {
        inventoryItems
                .findBy(text(productName))
                .$(".inventory_item_name") // clickable product link
                .click();
        return new ProductDetailPage();
    }

    /**
     * Sort products using visible text option (e.g. "Name (Z to A)")
     */
    public ProductsPage sortBy(String optionText) {
        sortDropdown.selectOption(optionText);
        return this;
    }

    /**
     * Get the list of product names in their current order
     */
    public List<String> getProductNames() {
        return productNames.texts();
    }

    public List<Double> getProductPrices() {
        return $$(".inventory_item_price").texts().stream()
                .map(price -> Double.parseDouble(price.replace("$", "")))
                .collect(Collectors.toList());
    }

    /**
     * Logs out from the application
     */
    public LoginPage logout() {
        menuButton.click();
        logoutLink.click();
        return new LoginPage();
    }

    public CartPage openCart() {
        cartIcon.click();
        return new CartPage();
    }

    public String getProductsTitle() {
        return pageTitle.getText();
    }

    public String getCurrentUrl() {
        return WebDriverRunner.url();
    }

}
