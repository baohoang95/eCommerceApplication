package com.example.demo;

import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit test class for {@link UserController}.
 * This class tests the functionality of the UserController, including:
 * - Finding users by ID or username.
 * - Creating new users with validation.
 */
public class UserControllerTest {

    private UserController userController;

    // Mock Repositories and Utilities
    private UserRepository userRepository = mock(UserRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);
    private BCryptPasswordEncoder bCryptPasswordEncoder = mock(BCryptPasswordEncoder.class);

    // Test constants
    private static final long USER_ID = 1;
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final int ITEM_ID = 1;
    private static final String ITEM_NAME = "item name";
    private static final String ITEM_DESCRIPTION = "item description";
    private static final BigDecimal PRICE = new BigDecimal(10);
    private static final BigDecimal TOTAL = new BigDecimal(20);

    // Mocked data
    private Item item = new Item();
    private User user = new User();
    private Cart cart = new Cart();

    /**
     * Initializes the test setup with mock data and dependencies.
     */
    @Before
    public void init() {
        userController = new UserController();

        TestUtils.injectObjects(userController, "userRepository", userRepository);
        TestUtils.injectObjects(userController, "cartRepository", cartRepository);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", bCryptPasswordEncoder);

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

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername(USERNAME)).thenReturn(user);
    }

    /**
     * Test finding a user by ID successfully.
     */
    @Test
    public void find_by_id_success() {
        ResponseEntity<User> responseResult = userController.findById(USER_ID);
        User user = responseResult.getBody();

        assertNotNull(responseResult);
        assertEquals(responseResult.getStatusCodeValue(), HttpStatus.OK.value());

        assertEquals(user.getUsername(), USERNAME);
        assertEquals(user.getCart(), cart);
        assertEquals(user.getPassword(), PASSWORD);
    }

    /**
     * Test failure when a user is not found by ID.
     */
    @Test
    public void find_by_id_fail_not_found_user() {
        ResponseEntity<User> responseResult = userController.findById(0L);

        assertNotNull(responseResult);
        assertEquals(responseResult.getStatusCodeValue(), HttpStatus.NOT_FOUND.value());
    }

    /**
     * Test finding a user by username successfully.
     */
    @Test
    public void find_by_name_success() {
        ResponseEntity<User> responseResult = userController.findByUserName(USERNAME);

        assertNotNull(responseResult);
        assertEquals(responseResult.getStatusCodeValue(), HttpStatus.OK.value());

        assertEquals(user.getUsername(), USERNAME);
        assertEquals(user.getCart(), cart);
        assertEquals(user.getPassword(), PASSWORD);
    }

    /**
     * Test failure when a user is not found by username.
     */
    @Test
    public void find_by_name_not_found_user() {
        ResponseEntity<User> responseResult = userController.findByUserName("");

        assertNotNull(responseResult);
        assertEquals(responseResult.getStatusCodeValue(), HttpStatus.NOT_FOUND.value());
    }

    /**
     * Test creating a user successfully.
     */
    @Test
    public void create_user_success() {
        when(bCryptPasswordEncoder.encode("password")).thenReturn("encodedPassword");

        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername(USERNAME);
        createUserRequest.setPassword(PASSWORD);
        createUserRequest.setConfirmPassword(PASSWORD);

        ResponseEntity<User> responseResult = userController.createUser(createUserRequest);
        User user = responseResult.getBody();

        assertNotNull(responseResult);
        assertEquals(responseResult.getStatusCodeValue(), HttpStatus.OK.value());

        assertEquals(user.getUsername(), USERNAME);
        assertEquals(user.getPassword(), "encodedPassword");
    }

    /**
     * Test failure when creating a user with a password that is too short.
     */
    @Test
    public void create_user_fail_password_length() {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername(USERNAME);
        createUserRequest.setPassword("123456");
        createUserRequest.setConfirmPassword("123456");

        ResponseEntity<User> responseResult = userController.createUser(createUserRequest);

        assertNotNull(responseResult);
        assertEquals(responseResult.getStatusCodeValue(), HttpStatus.BAD_REQUEST.value());
    }
}
