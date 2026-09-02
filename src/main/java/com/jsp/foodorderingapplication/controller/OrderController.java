package com.jsp.foodorderingapplication.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jsp.foodorderingapplication.dto.ResponseStructure;
import com.jsp.foodorderingapplication.entity.Order;
import com.jsp.foodorderingapplication.service.OrderService;

@RestController
@RequestMapping("/order")
public class OrderController {
	@Autowired
	private OrderService orderService;

	@PostMapping("/place")
	public ResponseEntity<ResponseStructure<Order>> placeOrder(@RequestBody Order order) {
		return new ResponseEntity<>(orderService.placeOrder(order), HttpStatus.CREATED);
	}

	@GetMapping("/all")
	public ResponseEntity<ResponseStructure<List<Order>>> getAllOrders() {

		return new ResponseEntity<>(orderService.getAllOrders(), HttpStatus.OK);
	}

	@GetMapping("/customer/{customerId}")
	public ResponseEntity<ResponseStructure<List<Order>>> getAllOrdersOfCustomer(@PathVariable Integer customerId) {

		return new ResponseEntity<>(orderService.getAllOrdersOfCustomer(customerId), HttpStatus.OK);

	}

	@GetMapping("/{id}")
	public ResponseEntity<ResponseStructure<Order>> getOrderById(@PathVariable Integer id) {

		return new ResponseEntity<>(orderService.getOrderById(id), HttpStatus.OK);

	}

	@PatchMapping("/{id}/status")
	public ResponseEntity<ResponseStructure<String>> updateOrderStatus(@RequestBody Map<String, Object> data,
			@PathVariable Integer id) {

		return new ResponseEntity<>(orderService.updateOrderStatus(data, id), HttpStatus.OK);

	}

	@PatchMapping("/{id}/cancel")
	public ResponseEntity<ResponseStructure<String>> cancelOrder(@PathVariable Integer id) {

		return new ResponseEntity<>(orderService.cancelOrder(id), HttpStatus.OK);

	}

	@GetMapping("/status/{status}")
	public ResponseEntity<ResponseStructure<List<Order>>> getOrdersByStatus(@PathVariable String status) {

		return new ResponseEntity<>(orderService.getOrdersByStatus(status), HttpStatus.OK);

	}

	@GetMapping("/date/{date}")
	public ResponseEntity<ResponseStructure<List<Order>>> getOrdersByDate(@PathVariable LocalDate date) {

		return new ResponseEntity<>(orderService.getOrdersByDate(date), HttpStatus.OK);

	}

	@GetMapping("/restaurant/{restaurantId}")
	public ResponseEntity<ResponseStructure<List<Order>>> getAllOrdersPlacedInRestaurant(
			@PathVariable Integer restaurantId) {

		return new ResponseEntity<>(orderService.getAllOrdersPlacedInRestaurant(restaurantId), HttpStatus.OK);

	}

}
