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
import com.jsp.foodorderingapplication.entity.Customer;
import com.jsp.foodorderingapplication.service.CustomerService;

@RestController
@RequestMapping("/customer")
public class CustomerController {
	
	@Autowired
	private CustomerService customerService;
	
	@PostMapping
	public ResponseEntity<ResponseStructure<Customer>> saveCustomer(@RequestBody Customer customer){
		return new ResponseEntity<>(customerService.saveCustomer(customer),HttpStatus.CREATED);
	}
	@GetMapping("/all")
	public ResponseEntity<ResponseStructure<List<Customer>>> getAllCustomers( ){
		return new ResponseEntity<>(customerService.getAllCustomers(),HttpStatus.OK);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<ResponseStructure<Customer>> getCustomerById(@PathVariable Integer id){
		return new ResponseEntity<>(customerService.getCustomerById(id),HttpStatus.OK);
	}
	
	@PatchMapping("/{id}")
	public ResponseEntity<ResponseStructure<String>> updateCustomer(@RequestBody Map<String, Object> data,@PathVariable Integer id){
		return new ResponseEntity<>(customerService.updateCustomer(data,id),HttpStatus.OK);
	}
	@DeleteMapping("/{id}")
	public ResponseEntity<ResponseStructure<String>> deleteCustomerById(@PathVariable Integer id){
		return new ResponseEntity<>(customerService.deleteCustomerByID(id),HttpStatus.OK);
	}
	
	@GetMapping("/contact/{contact}")
	public ResponseEntity<ResponseStructure<Customer>> getCustomerByContact(@PathVariable Long contact){
		return new ResponseEntity<>(customerService.getCustomerByContact(contact),HttpStatus.OK);
	}
	@GetMapping("/email/{email}")
	public ResponseEntity<ResponseStructure<Customer>> getCustomerByEmail(@PathVariable String email){
		return new ResponseEntity<>(customerService.getCustomerByEmail(email),HttpStatus.OK);
	}
	@GetMapping("/name/{name}")
	public ResponseEntity<ResponseStructure<List<Customer>>> getCustomerByName(@PathVariable String name){
		return new ResponseEntity<>(customerService.getCustomerByName(name),HttpStatus.OK);
	}
	
	

}
