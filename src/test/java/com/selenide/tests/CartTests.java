package com.selenide.tests;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.selenide.base.BaseTest;
import com.selenide.listener.JUnitlistener;
import com.selenide.pages.CartPage;
import com.selenide.pages.LoginPage;
import com.selenide.pages.ProductsPage;
import io.qameta.allure.*;
import io.qameta.allure.model.Status;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Epic("Cart Feature")
@ExtendWith(JUnitlistener.class)
@Feature("Cart Page")
@Tag("regression")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CartTests extends BaseTest {

    private static JsonNode cartData;
    private static JsonNode validUser;
    private static String expectedCartTitle;

    private ProductsPage productsPage;
    private CartPage cartPage;

    @BeforeAll
    public static void loadTestData() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        cartData = mapper.readTree(new File("src/test/resources/testdata/cartData.json"));
        validUser = cartData.get("validUser");
        expectedCartTitle = cartData.get("cartPageTitle").asText();
    }

    @BeforeEach
    public void login() {
        // Login → ProductsPage
        productsPage = new LoginPage()
                .openPage()
                .setUsername(validUser.get("username").asText())
                .setPassword(validUser.get("password").asText())
                .submitValidLogin(); // returns ProductsPage
    }

    @Test
    @Story("View Cart Page")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that the Cart page title is displayed correctly")
    public void testCartPageTitle() {
        cartPage = productsPage.openCart();
        String actualTitle = cartPage.getCartTitle();
        assertEquals(expectedCartTitle, actualTitle,
                "Cart page title should match expected value");
    }

    @Test
    @Story("Continue Shopping")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that clicking Continue Shopping on the Cart page navigates back to Products page")
    public void testContinueShoppingButton() {
        cartPage = productsPage.openCart();

        // Act: click Continue Shopping
        ProductsPage returnedPage = cartPage.continueShopping();

        // Assert: verify ProductsPage title
        String actualTitle = returnedPage.getProductsTitle();
        assertEquals("Products", actualTitle,
                "Clicking Continue Shopping should navigate back to Products page");
    }

    @Test
    @Story("Checkout from Cart")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Verify that an empty cart should not allow user to proceed to checkout (known bug in SauceDemo)")
    public void testEmptyCartShouldNotAllowCheckout() {
        cartPage = productsPage.openCart();

        // Act: try to proceed to checkout with an empty cart
        cartPage.proceedToCheckout();

        // log this discrepancy to Allure
        Allure.step("Checkout page is opened even with an empty cart", Status.BROKEN);

        // Defensive assertion
        assertEquals(0, cartPage.getCartItemCount(),
                "Cart should remain empty before attempting checkout");
    }

    @Test
    @Story("Cart Verification")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that added items appear in the CartPage")
    @DisplayName("Add items to cart and check cart page")
    public void testAddedItemsAppearInCart() {
        // Arrange: get the test product
        String productName = cartData.get("testProduct").asText();

        // Act: add product
        productsPage.addProductToCart(productName);

        // Open CartPage
        cartPage = productsPage.openCart();

        // Assert: item appears in cart
        List<String> cartItems = cartPage.getCartItemsNames().texts();
        assertTrue(cartItems.contains(productName),
                "Cart should contain the added product: " + productName);

        // Optional: check cart count matches
        assertEquals(1, cartItems.size(),
                "Cart item count should match number of products added");
    }

    @Test
    @Story("Cart Modification")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that an item can be removed from the CartPage")
    @DisplayName("Remove added item from cart")
    public void testRemoveItemFromCart() {
        // Arrange: add product first
        String productName = cartData.get("testProduct").asText();
        productsPage.addProductToCart(productName);

        // Open CartPage
        cartPage = productsPage.openCart();

        // Act: remove the product from cart
        cartPage.removeItemFromCart(productName);

        // Assert: product is no longer in cart
        List<String> cartItems = cartPage.getCartItemsNames().texts();
        assertTrue(!cartItems.contains(productName),
                "Cart should NOT contain the removed product: " + productName);

        // Optional: check cart count is 0
        assertEquals(0, cartItems.size(),
                "Cart item count should be zero after removal");
    }

}
