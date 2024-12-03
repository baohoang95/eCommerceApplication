package com.example.demo.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@RestController
@RequestMapping("/api/item")
public class ItemController {

	private static final Logger log = LogManager.getLogger(ItemController.class);

	@Autowired
	private ItemRepository itemRepository;
	
	@GetMapping
	public ResponseEntity<List<Item>> getItems() {
		List<Item> result = itemRepository.findAll();
		log.info("Successfully found all items");
		return ResponseEntity.ok(result);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Item> getItemById(@PathVariable Long id) {
		log.info("Start get items by id {}", id);

		Optional<Item> result = itemRepository.findById(id);
		log.info("Successfully found item by id");

        log.info("End get item by id");
		return ResponseEntity.of(result);
	}
	
	@GetMapping("/name/{name}")
	public ResponseEntity<List<Item>> getItemsByName(@PathVariable String name) {
		log.info("Start get items by item name {}", name);

		List<Item> items = itemRepository.findByName(name);
		if (items == null || items.isEmpty()) {
			log.error("Failed to find item {}", name);
			return ResponseEntity.notFound().build();
		}

		log.info("Successfully found item {}", name);
		return ResponseEntity.ok(items);
	}
}
