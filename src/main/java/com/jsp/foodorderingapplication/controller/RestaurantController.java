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
import com.jsp.foodorderingapplication.entity.Restaurant;
import com.jsp.foodorderingapplication.service.RestaurantService;

@RestController
@RequestMapping("/restaurant")
public class RestaurantController {

	@Autowired
	private RestaurantService restaurantService;
	@PostMapping
	public ResponseEntity<ResponseStructure<Restaurant>> saveRestaurant(@RequestBody Restaurant restaurant){
	
		return new ResponseEntity<>(restaurantService.saveRestaurant(restaurant),HttpStatus.CREATED);
	}
	
	@GetMapping("/all")
	public ResponseEntity<ResponseStructure<List<Restaurant>>> fetchAllRestaurants(){
		return new ResponseEntity<>(restaurantService.fetchAllRestaurants(),HttpStatus.OK);
	}
	@GetMapping("/{id}")
	public ResponseEntity<ResponseStructure<Restaurant>> findRestaurantId(@PathVariable Integer id){
	
		return new ResponseEntity<>(restaurantService.findRestaurantId(id),HttpStatus.OK);
	}
	@PatchMapping("/update/{id}")
	public ResponseEntity<ResponseStructure<String>> updateRestaurant(@RequestBody Map<String,Object> data, @PathVariable Integer id){
		return new ResponseEntity<>(restaurantService.updateRestaurant(data,id),HttpStatus.OK);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<ResponseStructure<String>> deleteRestaurantById( @PathVariable Integer id){
		return new ResponseEntity<>(restaurantService.deleteRestaurantByID(id),HttpStatus.OK);
	}
	

	@GetMapping("/location/{location}")
	public ResponseEntity<ResponseStructure<List<Restaurant>>> fetchRestaurantByLocation(@PathVariable String location){
		return new ResponseEntity<>(restaurantService.fetchRestaurantByLocation(location),HttpStatus.OK);
	}
	@GetMapping("/name/{name}")
	public ResponseEntity<ResponseStructure<List<Restaurant>>> fetchRestaurantByName(@PathVariable String name){
		return new ResponseEntity<>(restaurantService.fetchRestaurantByName(name),HttpStatus.OK);
	}
	@GetMapping("/greaterthan/rating/{rating}")
	public ResponseEntity<ResponseStructure<List<Restaurant>>> getRestaurantsByRatingGreaterThan(@PathVariable Integer rating){
		return new ResponseEntity<>(restaurantService.getRestaurantsByRatingGreaterThan(rating),HttpStatus.OK);
	}
	
	@GetMapping("/menuitem/{id}")
	public ResponseEntity<ResponseStructure<List<MenuItem>>> getMenuOfRestaurant(@PathVariable Integer id){
		return new ResponseEntity<>(restaurantService.getMenuOfRestaurant(id),HttpStatus.OK);
	}
}
