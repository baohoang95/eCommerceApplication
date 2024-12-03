package com.example.demo;

import com.example.demo.controllers.ItemController;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit test class for {@link ItemController}.
 * This class tests the functionality of the ItemController, including:
 * - Retrieving items by ID.
 * - Retrieving items by name.
 */
public class ItemControllerTest {

    private ItemController itemController;

    // Mock repository for Item
    private ItemRepository itemRepository = mock(ItemRepository.class);

    // Test constants
    private static final long ITEM_ID = 1;
    private static final String ITEM_NAME = "item name";
    private static final String ITEM_DESCRIPTION = "item description";
    private static final BigDecimal PRICE = new BigDecimal(10);

    /**
     * Initializes the test setup with mock data and dependencies.
     */
    @Before
    public void init() {
        itemController = new ItemController();

        TestUtils.injectObjects(itemController, "itemRepository", itemRepository);

        // Mock item data
        Item item_1 = new Item();
        item_1.setId(ITEM_ID);
        item_1.setName(ITEM_NAME);
        item_1.setPrice(PRICE);
        item_1.setDescription(ITEM_DESCRIPTION);

        Item item_2 = new Item();
        item_2.setId(ITEM_ID + 1);
        item_2.setName(ITEM_NAME);
        item_2.setPrice(PRICE);
        item_2.setDescription(ITEM_DESCRIPTION);

        List<Item> items = new ArrayList<>();
        items.add(item_1);
        items.add(item_2);

        // Mock repository methods
        when(itemRepository.findById(ITEM_ID)).thenReturn(Optional.of(item_1));
        when(itemRepository.findByName(ITEM_NAME)).thenReturn(items);
    }

    /**
     * Test retrieving an item by ID successfully.
     */
    @Test
    public void get_item_by_id_success() {
        ResponseEntity<Item> responseResult = itemController.getItemById(ITEM_ID);
        Item item = responseResult.getBody();

        assertNotNull(responseResult);
        assertEquals(responseResult.getStatusCodeValue(), HttpStatus.OK.value());

        assertNotNull(item);
        assertEquals(item.getName(), ITEM_NAME);
        assertEquals(item.getDescription(), ITEM_DESCRIPTION);
        assertEquals(item.getPrice(), PRICE);
    }

    /**
     * Test failure when retrieving an item by a non-existent ID.
     */
    @Test
    public void get_item_by_id_fail_not_found_item() {
        ResponseEntity<Item> responseResult = itemController.getItemById(ITEM_ID + 2);

        assertNotNull(responseResult);
        assertEquals(responseResult.getStatusCodeValue(), HttpStatus.NOT_FOUND.value());
    }

    /**
     * Test retrieving items by name successfully.
     */
    @Test
    public void get_item_by_name_success() {
        ResponseEntity<List<Item>> responseResult = itemController.getItemsByName(ITEM_NAME);
        List<Item> items = responseResult.getBody();

        assertNotNull(responseResult);
        assertEquals(responseResult.getStatusCodeValue(), HttpStatus.OK.value());

        for (Item item : items) {
            assertNotNull(item);
            assertEquals(item.getName(), ITEM_NAME);
            assertEquals(item.getDescription(), ITEM_DESCRIPTION);
            assertEquals(item.getPrice(), PRICE);
        }
    }

    /**
     * Test failure when retrieving items by a non-existent name.
     */
    @Test
    public void get_item_by_name_fail_not_found_item() {
        ResponseEntity<List<Item>> responseResult = itemController.getItemsByName("");

        assertNotNull(responseResult);
        assertEquals(responseResult.getStatusCodeValue(), HttpStatus.NOT_FOUND.value());
    }
}
