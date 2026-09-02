package com.jsp.foodorderingapplication.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jsp.foodorderingapplication.dto.LoginResponse;
import com.jsp.foodorderingapplication.dto.ResponseStructure;
import com.jsp.foodorderingapplication.entity.Customer;
import com.jsp.foodorderingapplication.entity.Restaurant;
import com.jsp.foodorderingapplication.service.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {

	@Autowired
	private AuthService authService;

	@PostMapping("/customer/register")
	public ResponseEntity<ResponseStructure<Customer>> registerCustomer(@RequestBody Customer customer) {
		return new ResponseEntity<>(authService.registerCustomer(customer), HttpStatus.CREATED);
	}

	@PostMapping("/restaurant/register")
	public ResponseEntity<ResponseStructure<Restaurant>> registerRestaurant(@RequestBody Restaurant restaurant) {
		return new ResponseEntity<>(authService.registerRestaurant(restaurant), HttpStatus.CREATED);
	}

	@PostMapping("/login")
	public ResponseEntity<ResponseStructure<LoginResponse>> login(@RequestBody Map<String, String> data) {
		return new ResponseEntity<>(authService.login(data.get("email"), data.get("password"), data.get("role")),
				HttpStatus.OK);
	}

	@PostMapping("/forgot-password")
	public ResponseEntity<ResponseStructure<String>> forgotPassword(@RequestBody Map<String, String> data) {

		return new ResponseEntity<>(authService.generateResetToken(data.get("email"), data.get("role")), HttpStatus.OK);
	}

	@PostMapping("/reset-password")
	public ResponseEntity<ResponseStructure<String>> resetPassword(@RequestBody Map<String, String> data) {

		return new ResponseEntity<>(authService.resetPassword(data.get("token"), data.get("newPassword")),
				HttpStatus.OK);
	}
}
