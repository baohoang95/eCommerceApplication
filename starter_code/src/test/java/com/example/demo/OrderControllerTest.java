package com.example.demo;

import com.example.demo.controllers.OrderController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit test class for {@link OrderController}.
 * This class tests the functionality of OrderController, including:
 * - Submitting orders for a user.
 * - Retrieving all orders for a user.
 */
public class OrderControllerTest {

    private OrderController orderController;

    // Mock repositories
    private UserRepository userRepository = mock(UserRepository.class);
    private OrderRepository orderRepository = mock(OrderRepository.class);

    // Test constants
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final int ITEM_ID = 1;
    private static final String ITEM_NAME = "item name";
    private static final String ITEM_DESCRIPTION = "item description";
    private static final BigDecimal PRICE = new BigDecimal(10);
    private static final BigDecimal TOTAL = new BigDecimal(20);

    private List<UserOrder> userOrders;
    private Item item = new Item();
    private User user = new User();
    private Cart cart = new Cart();

    /**
     * Initializes the test setup with mock data and dependencies.
     */
    @Before
    public void init() {
        orderController = new OrderController();

        TestUtils.injectObjects(orderController, "userRepository", userRepository);
        TestUtils.injectObjects(orderController, "orderRepository", orderRepository);

        // Mock item and cart setup
        item.setId((long) ITEM_ID);
        item.setName(ITEM_NAME);
        item.setPrice(PRICE);
        item.setDescription(ITEM_DESCRIPTION);

        cart.setItems(Arrays.asList(item));
        cart.setTotal(TOTAL);
        cart.setUser(user);

        user.setUsername(USERNAME);
        user.setPassword(PASSWORD);
        user.setCart(cart);

        // Mock orders
        UserOrder userOrder_1 = getUserOrder(1);
        UserOrder userOrder_2 = getUserOrder(2);
        userOrders = new ArrayList<>();
        userOrders.add(userOrder_1);
        userOrders.add(userOrder_2);

        // Mock repository methods
        when(userRepository.findByUsername(USERNAME)).thenReturn(user);
        when(orderRepository.findByUser(user)).thenReturn(userOrders);
    }

    /**
     * Test submitting an order successfully for an existing user.
     */
    @Test
    public void submit_success() {
        ResponseEntity<UserOrder> responseResult = orderController.submit(USERNAME);
        UserOrder userOrder = responseResult.getBody();

        assertNotNull(responseResult);
        assertEquals(responseResult.getStatusCodeValue(), HttpStatus.OK.value());

        assertNotNull(userOrder);
        assertEquals(userOrder.getItems().get(0), item);
        assertEquals(userOrder.getTotal(), TOTAL);
        assertEquals(userOrder.getUser(), user);
    }

    /**
     * Test failure when submitting an order for a non-existent user.
     */
    @Test
    public void submit_fail_not_found_user() {
        ResponseEntity<UserOrder> responseResult = orderController.submit("");

        assertNotNull(responseResult);
        assertEquals(responseResult.getStatusCodeValue(), HttpStatus.NOT_FOUND.value());
    }

    /**
     * Test retrieving all orders for an existing user successfully.
     */
    @Test
    public void get_orders_for_user_success() {
        ResponseEntity<List<UserOrder>> responseResult = orderController.getOrdersForUser(USERNAME);

        List<UserOrder> userOrdersResult = responseResult.getBody();

        assertNotNull(responseResult);
        assertEquals(responseResult.getStatusCodeValue(), HttpStatus.OK.value());

        assertNotNull(userOrdersResult);

        for (int i = 0; i < userOrdersResult.size(); i++) {
            assertEquals(userOrdersResult.get(i), userOrders.get(i));
        }
    }

    /**
     * Test failure when retrieving orders for a non-existent user.
     */
    @Test
    public void get_orders_for_user_fail_not_found_user() {
        ResponseEntity<List<UserOrder>> responseResult = orderController.getOrdersForUser("");

        assertNotNull(responseResult);
        assertEquals(responseResult.getStatusCodeValue(), HttpStatus.NOT_FOUND.value());
    }

    /**
     * Helper method to create a mock UserOrder.
     *
     * @param id the ID of the order.
     * @return a UserOrder object.
     */
    private UserOrder getUserOrder(long id) {
        UserOrder userOrder = new UserOrder();
        userOrder.setId(id);
        userOrder.setUser(user);
        userOrder.setItems(Arrays.asList(item));
        userOrder.setTotal(TOTAL);

        return userOrder;
    }
}
