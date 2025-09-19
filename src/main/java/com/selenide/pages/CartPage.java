package com.selenide.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Condition.text;

public class CartPage {

    // Locators
    private final SelenideElement cartTitle = $(".title");
    private final ElementsCollection cartItems = $$(".cart_item");
    private final SelenideElement checkoutButton = $("#checkout");
    private final SelenideElement continueShoppingButton = $("#continue-shopping");
    private final ElementsCollection cartItemNames = $$(".inventory_item_name");




     // Verify the Cart page title

    public String getCartTitle() {
        return cartTitle.getText();
    }

      // Get the number of items in the cart

    public int getCartItemCount() {
        return cartItems.size();
    }

    /**
     * Verify a product exists in the cart
     */
    public boolean isProductInCart(String productName) {
        return cartItems.findBy(text(productName)).exists();
    }

    /**
     * Remove a product from the cart by product name
     */
    public CartPage removeProduct(String productName) {
        cartItems.findBy(text(productName))
                .$("button")
                .click();
        return this;
    }

    /**
     * Proceed to checkout
     */
    public CheckoutPage proceedToCheckout() {
        checkoutButton.click();
        return new CheckoutPage();
    }

    /**
     * Go back to products page
     */
    public ProductsPage continueShopping() {
        continueShoppingButton.click();
        return new ProductsPage();
    }


    public CartPage removeItemFromCart(String productName) {
        $$(".cart_item").findBy(Condition.text(productName))
                .$("button").click();
        return this;
    }

    public void assertItemNotInCart(String productName) {
        $$(".cart_item .inventory_item_name")
                .findBy(Condition.text(productName))
                .shouldNot(Condition.exist);
    }

    public ElementsCollection getCartItemsNames() {
        return $$(".cart_item .inventory_item_name");
    }


    public CheckoutPage checkout() {
        checkoutButton.click();
        return new CheckoutPage();
    }
}



