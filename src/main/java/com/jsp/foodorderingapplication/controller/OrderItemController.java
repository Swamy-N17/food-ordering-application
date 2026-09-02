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
import com.jsp.foodorderingapplication.entity.OrderItem;
import com.jsp.foodorderingapplication.service.OrderItemService;

@RestController
@RequestMapping("/orderitem")
public class OrderItemController {

	@Autowired
	private OrderItemService orderItemService;

	@PostMapping()
	public ResponseEntity<ResponseStructure<OrderItem>> createOrderItem(@RequestBody OrderItem orderItem) {

		return new ResponseEntity<>(orderItemService.createOrderItem(orderItem), HttpStatus.CREATED);
	}

	@PatchMapping("/quantity/{id}")
	public ResponseEntity<ResponseStructure<String>> updateItemQuantity(@RequestBody Map<String, Object> data,
			@PathVariable Integer id) {

		return new ResponseEntity<>(orderItemService.updateItemQuantity(data, id), HttpStatus.OK);
	}

	@DeleteMapping("/{orderItemId}")
	public ResponseEntity<ResponseStructure<String>> removeItemFromOrder(@PathVariable Integer orderItemId) {

		return new ResponseEntity<>(orderItemService.removeItemFromOrder(orderItemId), HttpStatus.OK);

	}

	@GetMapping("/order/all/{orderId}")
	public ResponseEntity<ResponseStructure<List<OrderItem>>> getAllOrderItemsOfOrder(@PathVariable Integer orderId) {

		return new ResponseEntity<>(orderItemService.getAllOrderItemsOfOrder(orderId), HttpStatus.OK);

	}

}
