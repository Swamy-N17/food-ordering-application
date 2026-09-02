package com.jsp.foodorderingapplication.service;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.jsp.foodorderingapplication.Enum.OrderStatus;
import com.jsp.foodorderingapplication.dto.ResponseStructure;
import com.jsp.foodorderingapplication.entity.Customer;
import com.jsp.foodorderingapplication.entity.Order;
import com.jsp.foodorderingapplication.exception.ContactVerificationException;
import com.jsp.foodorderingapplication.exception.IdNotFoundException;
import com.jsp.foodorderingapplication.exception.InvalidDataException;
import com.jsp.foodorderingapplication.exception.InvalidFieldException;
import com.jsp.foodorderingapplication.exception.NoRecordAvailableException;
import com.jsp.foodorderingapplication.repository.CustomerRepository;

@Service
public class CustomerService {

	@Autowired
	private CustomerRepository customerRepository;

	public ResponseStructure<Customer> saveCustomer(Customer customer) {

		ResponseStructure<Customer> res = new ResponseStructure<Customer>();

		// Contact length Validation
		if (String.valueOf(customer.getContact()).length() != 10)
			throw new ContactVerificationException("Contact Number Must Contain 10 Digits");

		// contact unique validation
		if (customerRepository.existsByContact(customer.getContact()) == true)
			throw new DataIntegrityViolationException("Contact already exists");
		// Email validation
		if (customerRepository.existsByEmail(customer.getEmail()) == true)
			throw new DataIntegrityViolationException("Email already exists ");

		res.setMessage("Customer Record Saved Sucessfully");
		res.setStatusCode(HttpStatus.CREATED.value());
		res.setData(customerRepository.save(customer));

		return res;
	}

	public ResponseStructure<List<Customer>> getAllCustomers() {

		ResponseStructure<List<Customer>> res = new ResponseStructure<>();
		List<Customer> customer = customerRepository.findAll();
		if (customer.isEmpty())
			throw new NoRecordAvailableException("No Record Found");

		res.setMessage("Customer Record Saved Sucessfully");
		res.setStatusCode(HttpStatus.OK.value());
		res.setData(customer);

		return res;
	}

	public ResponseStructure<Customer> getCustomerById(Integer id) {

		ResponseStructure<Customer> res = new ResponseStructure<Customer>();
		Optional<Customer> opt = customerRepository.findById(id);
		if (opt.isPresent()) {
			res.setMessage("Customer Record With ID: " + id + " Fetched Sucessfully");
			res.setStatusCode(HttpStatus.OK.value());
			res.setData(opt.get());
			return res;
		}

		throw new IdNotFoundException("Customer With ID: " + id + " Not Found");

	}

	public ResponseStructure<String> updateCustomer(@RequestBody Map<String, Object> data, Integer id) {

		ResponseStructure<String> res = new ResponseStructure<String>();
		Optional<Customer> opt = customerRepository.findById(id);
		if (opt.isPresent()) {
			Customer customer = opt.get();
			for (Map.Entry<String, Object> entry : data.entrySet()) {
				String key = entry.getKey();
				Object value = entry.getValue();

				switch (key) {
				case "name":
					customer.setName((String) value);
					break;
				case "email":
					customer.setEmail((String) value);
					break;
				case "contact":
					customer.setContact(((Number) value).longValue());
					break;
				case "address":
					customer.setAddress((String) value);
					break;
				default:
					throw new InvalidFieldException("Invalid field: " + key);
				}
			}
			customerRepository.save(customer);
			res.setMessage("Customer Record With ID: " + id + " Updated Sucessfully");
			res.setStatusCode(HttpStatus.OK.value());
			res.setData("Success");
			return res;
		}

		throw new IdNotFoundException("Customer With ID: " + id + " Not Found");

	}

	public ResponseStructure<String> deleteCustomerByID(Integer id) {

		ResponseStructure<String> res = new ResponseStructure<String>();
		Optional<Customer> opt = customerRepository.findById(id);
		if (opt.isPresent()) {
			Iterator<Order> items = opt.get().getOrders().iterator();

			while (items.hasNext()) {

				if (!(items.next().getStatus().equals(OrderStatus.DELIVERED)))
					throw new InvalidDataException("Customer cannot be deleted because they have incomplete orders");
			}
			customerRepository.deleteById(id);
			res.setMessage("Customer Record With ID: " + id + " Deleted Sucessfully");
			res.setStatusCode(HttpStatus.OK.value());
			res.setData("Success");
			return res;

		}

		throw new IdNotFoundException("Customer With ID: " + id + " Not Found");

	}

	public ResponseStructure<Customer> getCustomerByContact(Long contact) {

		ResponseStructure<Customer> res = new ResponseStructure<Customer>();
		Optional<Customer> opt = customerRepository.findByContact(contact);
		if (opt.isPresent()) {
			res.setMessage("Customer Record With Contact: " + contact + " Fetched Sucessfully");
			res.setStatusCode(HttpStatus.OK.value());
			res.setData(opt.get());
			return res;
		}

		throw new IdNotFoundException("Customer With Contact: " + contact + " Not Found");

	}

	public ResponseStructure<Customer> getCustomerByEmail(String email) {
		ResponseStructure<Customer> res = new ResponseStructure<Customer>();
		Optional<Customer> opt = customerRepository.findByEmail(email);
		if (opt.isPresent()) {
			res.setMessage("Customer Record With Email: " + email + " Fetched Sucessfully");
			res.setStatusCode(HttpStatus.OK.value());
			res.setData(opt.get());
			return res;
		}

		throw new IdNotFoundException("Customer With Email: " + email + " Not Found");

	}

	public ResponseStructure<List<Customer>> getCustomerByName(String name) {
		ResponseStructure<List<Customer>> res = new ResponseStructure<>();
		List<Customer> customer = customerRepository.findByName(name);
		if (!customer.isEmpty()) {
			res.setMessage("Customers Record Fetched Sucessfully");
			res.setStatusCode(HttpStatus.OK.value());
			res.setData(customer);
			return res;
		}

		throw new NoRecordAvailableException("No Record Found");

	}

}
