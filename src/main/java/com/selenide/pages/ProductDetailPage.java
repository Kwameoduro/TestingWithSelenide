package com.selenide.pages;



import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;


public class ProductDetailPage {

    // Locators
    private final SelenideElement productTitle = $(".inventory_details_name");
    private final SelenideElement productDescription = $(".inventory_details_desc");
    private final SelenideElement productPrice = $(".inventory_details_price");
    private final SelenideElement addToCartButton = $(".btn_primary.btn_inventory");
    private final SelenideElement removeFromCartButton = $(".btn_secondary.btn_inventory");
    private final SelenideElement backButton = $("#back-to-products");
    private final ElementsCollection inventoryItems = $$(".inventory_item");



     // Get product title

    public String getProductTitle() {
        return productTitle.getText();
    }

      // Get product description

    public String getProductDescription() {
        return productDescription.getText();
    }


     //  Get product price

    public String getProductPrice() {
        return productPrice.getText();
    }

    /**
     * Add product to cart
     */
    public ProductDetailPage addToCart() {
        addToCartButton.click();
        return this;
    }

    /**
     * Remove product from cart
     */
    public ProductDetailPage removeFromCart() {
        removeFromCartButton.click();
        return this;
    }

    /**
     * Navigate back to products page
     */
    public ProductsPage backToProducts() {
        backButton.click();
        return new ProductsPage();
    }



}
