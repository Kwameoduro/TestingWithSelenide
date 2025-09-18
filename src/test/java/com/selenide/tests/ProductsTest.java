package com.selenide.tests;





import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.selenide.base.BaseTest;
import com.selenide.listener.JUnitlistener;
import com.selenide.pages.LoginPage;
import com.selenide.pages.ProductDetailPage;
import com.selenide.pages.ProductsPage;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Epic("Products Feature")
@ExtendWith(JUnitlistener.class)
@Feature("Products Page")
@Tag("smoke")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductsTest extends BaseTest {

    static JsonNode productData;
    private static JsonNode validUser;

    private ProductsPage productsPage;

    @BeforeAll
    public static void loadTestData() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        productData = mapper.readTree(new File("src/test/resources/testdata/productData.json"));

        validUser = productData.get("validUser"); // ✅ validUser comes from productData.json
    }

    @BeforeEach
    public void loginAndNavigateToProductsPage() {
        // ✅ Login once, land directly on ProductsPage
        productsPage = new LoginPage()
                .openPage()
                .setUsername(validUser.get("username").asText())
                .setPassword(validUser.get("password").asText())
                .submitValidLogin(); // must return ProductsPage
    }

    @Test
    @Story("Verify Products Page Title")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Check that the Products page title is displayed correctly after a valid login")
    public void testProductsPageTitle() {
        // Arrange
        String expectedTitle = productData.get("productsPageTitle").asText();

        // Act
        String actualTitle = productsPage.getPageTitle();

        // Assert
        assertEquals(expectedTitle, actualTitle, "Products page title should match expected");
    }


    @Test
    @Story("Add to Cart from Products Page")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that a product can be added to the cart and the cart badge updates correctly")
    public void testAddProductToCart() {
        // Arrange
        String productName = productData.get("testProduct").asText();

        // Act
        productsPage.addProductToCart(productName);

        // Assert
        int cartCount = productsPage.getCartItemCount();
        assertEquals(1, cartCount, "Cart badge should show 1 item after adding a product");
    }

    @Test
    @Story("Remove Item from Cart")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Remove product from cart directly from Products Page")
    void testRemoveFromCartFromProductsPage() {
        // ✅ Fetch product name from test data
        String productName = productData.get("productNameToRemove").asText();

        // ✅ Add product to cart first
        productsPage.addProductToCart(productName);
        assertTrue(productsPage.isProductInCart(productName),
                "Product should be added to the cart before removal");

        // ✅ Remove the product
        productsPage.removeProductFromCart(productName);

        // 🔍 Assert product is no longer in cart
        assertFalse(productsPage.isProductInCart(productName),
                "Product should be removed from the cart");
    }

    @Test
    @Story("Add product to cart from Product Detail Page")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that a user can add a product to the cart from the product detail page")
    public void testAddToCartFromProductDetailPage() {
        // Arrange
        String productName = productData.get("productToAddFromDetail").asText();

        // Act: go to product detail page and add to cart
        ProductDetailPage detailPage = productsPage.openProductDetail(productName);
        detailPage.addToCart();

        // Assert: verify cart badge shows 1
        assertEquals("1", productsPage.getCartBadgeCount(),
                "Cart badge count should be 1 after adding from product detail page");

        // ✅ (Optional) verify product title matches expected
        assertEquals(productName, detailPage.getProductTitle(),
                "Product title on detail page should match selected product");
    }

    @Test
    @Story("Remove product from cart from Product Detail Page")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that a user can remove a product from the cart directly from the product detail page")
    public void testRemoveFromCartFromProductDetailPage() {
        // Arrange
        String productName = productData.get("productToAddFromDetail").asText();

        // Act: open product detail, add and then remove
        ProductDetailPage detailPage = productsPage.openProductDetail(productName);
        detailPage.addToCart()
                .removeFromCart();

        // Assert: verify cart badge disappears / resets to 0
        assertEquals("0", productsPage.getCartBadgeCount(),
                "Cart badge count should be 0 after removing product from detail page");

    }

    @Test
    @Story("Sort products by Name Z to A")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that products can be sorted by Name (Z to A) correctly")
    public void testSortProductsByNameZToA() {
        // Arrange: get product names in A→Z first
        List<String> unsortedNames = productsPage.getProductNames();
        List<String> expectedOrder = new ArrayList<>(unsortedNames);
        expectedOrder.sort(Comparator.reverseOrder());

        // Act: sort by Z→A in the UI
        productsPage.sortBy("Name (Z to A)");
        List<String> actualOrder = productsPage.getProductNames();

        // Assert
        assertEquals(expectedOrder, actualOrder,
                "Products should be sorted by Name Z to A correctly");
    }

    @Test
    @Story("Sort products by Price low to high")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that products can be sorted by Price (low to high) correctly")
    public void testSortProductsByPriceLowToHigh() {
        // Arrange: get product prices before sorting
        List<Double> unsortedPrices = productsPage.getProductPrices();
        List<Double> expectedOrder = new ArrayList<>(unsortedPrices);
        expectedOrder.sort(Comparator.naturalOrder());

        // Act: sort by Price (low → high) in the UI
        productsPage.sortBy("Price (low to high)");
        List<Double> actualOrder = productsPage.getProductPrices();

        // Assert
        assertEquals(expectedOrder, actualOrder,
                "Products should be sorted by Price (low to high) correctly");
    }


    @Test
    @Story("Sort products by Price high to low")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that products can be sorted by Price (high to low) correctly")
    public void testSortProductsByPriceHighToLow() {
        // Arrange: get product prices before sorting
        List<Double> unsortedPrices = productsPage.getProductPrices();
        List<Double> expectedOrder = new ArrayList<>(unsortedPrices);
        expectedOrder.sort(Comparator.reverseOrder()); // descending

        // Act: sort by Price (high → low) in the UI
        productsPage.sortBy("Price (high to low)");
        List<Double> actualOrder = productsPage.getProductPrices();

        // Assert
        assertEquals(expectedOrder, actualOrder,
                "Products should be sorted by Price (high to low) correctly");
    }


    @Test
    @Story("Logout from the application")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that the user can log out successfully from the Products page")
    public void testLogoutFromProductsPage() {
        // Act: perform logout
        LoginPage loginPage = productsPage.logout();

        // Assert: verify redirected back to login page
        assertTrue(loginPage.isLoginButtonDisplayed(),
                "User should be redirected to Login Page after logout");
    }

}
