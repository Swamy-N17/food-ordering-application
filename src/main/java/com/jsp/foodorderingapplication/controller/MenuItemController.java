package com.jsp.foodorderingapplication.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jsp.foodorderingapplication.dto.ResponseStructure;
import com.jsp.foodorderingapplication.entity.MenuItem;
import com.jsp.foodorderingapplication.service.MenuItemService;

@RestController
@RequestMapping("/menuitem")
public class MenuItemController {

	@Autowired
	private MenuItemService menuItemService;

	@PostMapping
	public ResponseEntity<ResponseStructure<MenuItem>> saveItems(@RequestBody MenuItem menuItem) {

		return new ResponseEntity<>(menuItemService.saveItems(menuItem), HttpStatus.CREATED);

	}

	@GetMapping("/all")
	public ResponseEntity<ResponseStructure<List<MenuItem>>> fetchAllItems() {
		return new ResponseEntity<>(menuItemService.fetchAllItems(), HttpStatus.OK);
	}

	@GetMapping("/{id}")
	public ResponseEntity<ResponseStructure<MenuItem>> getItemById(@PathVariable Integer id) {

		return new ResponseEntity<>(menuItemService.getItemById(id), HttpStatus.OK);

	}

	@PatchMapping("/{id}")
	public ResponseEntity<ResponseStructure<String>> updatePriceAndAvailability(@RequestBody Map<String, Object> data,
			@PathVariable Integer id) {

		return new ResponseEntity<>(menuItemService.updatePriceAndAvailability(data, id), HttpStatus.OK);

	}

	@GetMapping("/sortbyprice/{fieldName}")
	public ResponseEntity<ResponseStructure<List<MenuItem>>> sortByPrice(@PathVariable String fieldName) {
		return new ResponseEntity<>(menuItemService.sortByPrice(fieldName), HttpStatus.OK);
	}

	@GetMapping("/name/{name}")
	public ResponseEntity<ResponseStructure<List<MenuItem>>> getItemsByName(@PathVariable String name) {

		return new ResponseEntity<>(menuItemService.getItemsByName(name), HttpStatus.OK);
	}

	@GetMapping("/id/{restaurantId}")
	public ResponseEntity<ResponseStructure<List<MenuItem>>> getAllItemsInRestaurant(
			@PathVariable Integer restaurantId) {

		return new ResponseEntity<>(menuItemService.getAllItemsInRestaurant(restaurantId), HttpStatus.OK);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<ResponseStructure<String>> deleteMenuItem(@PathVariable Integer id) {

		ResponseStructure<String> response = menuItemService.deleteMenuItem(id);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
