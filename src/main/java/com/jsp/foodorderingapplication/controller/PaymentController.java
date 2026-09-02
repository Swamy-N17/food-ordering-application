package com.jsp.foodorderingapplication.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jsp.foodorderingapplication.dto.ResponseStructure;
import com.jsp.foodorderingapplication.entity.Payment;
import com.jsp.foodorderingapplication.service.PaymentService;

@RestController
@RequestMapping("/payment")
public class PaymentController {

	@Autowired
	private PaymentService paymentService;

	@GetMapping("/{id}")
	public ResponseEntity<ResponseStructure<Payment>> getPaymentById(@PathVariable Integer id) {

		return new ResponseEntity<>(paymentService.getPaymentById(id), HttpStatus.OK);
	}

	@GetMapping("/order/{orderId}")
	public ResponseEntity<ResponseStructure<Payment>> getPaymentByOrder(@PathVariable Integer orderId) {

		return new ResponseEntity<>(paymentService.getPaymentByOrder(orderId), HttpStatus.OK);
	}

	@GetMapping("/status/{status}")
	public ResponseEntity<ResponseStructure<List<Payment>>> getPaymentByStatus(@PathVariable String status) {

		return new ResponseEntity<>(paymentService.getPaymentByStatus(status), HttpStatus.OK);
	}

	@GetMapping("/methodtype/{methodType}")
	public ResponseEntity<ResponseStructure<List<Payment>>> getPaymentByMethod(@PathVariable String methodType) {

		return new ResponseEntity<>(paymentService.getPaymentByMethod(methodType), HttpStatus.OK);
	}

	@PatchMapping("/update/{paymentId}")
	public ResponseEntity<ResponseStructure<String>> updatePaymentStatus(@RequestBody Map<String, Object> data,
			@PathVariable Integer paymentId) {

		return new ResponseEntity<>(paymentService.updatePaymentStatus(data, paymentId), HttpStatus.OK);
	}
}
