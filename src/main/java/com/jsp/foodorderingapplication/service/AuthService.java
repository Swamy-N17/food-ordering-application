package com.jsp.foodorderingapplication.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.jsp.foodorderingapplication.dto.LoginResponse;
import com.jsp.foodorderingapplication.dto.ResponseStructure;
import com.jsp.foodorderingapplication.entity.Customer;
import com.jsp.foodorderingapplication.entity.Restaurant;
import com.jsp.foodorderingapplication.exception.InvalidDataException;
import com.jsp.foodorderingapplication.repository.CustomerRepository;
import com.jsp.foodorderingapplication.repository.PasswordResetTokenRepository;
import com.jsp.foodorderingapplication.repository.RestaurantRepository;
import java.time.LocalDateTime;
import java.util.UUID;

import com.jsp.foodorderingapplication.entity.PasswordResetToken;

@Service
public class AuthService {

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private RestaurantRepository restaurantRepository;

	@Autowired
	private PasswordResetTokenRepository passwordResetTokenRepository;

	@Autowired
	private EmailService emailService;

	private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	public ResponseStructure<Customer> registerCustomer(Customer customer) {

		ResponseStructure<Customer> res = new ResponseStructure<>();

		if (customer.getName() == null || customer.getName().isBlank())
			throw new InvalidDataException("Name is required");

		if (customer.getEmail() == null || customer.getEmail().isBlank())
			throw new InvalidDataException("Email is required");

		if (customer.getPassword() == null || customer.getPassword().isBlank())
			throw new InvalidDataException("Password is required");

		if (customer.getContact() == null || String.valueOf(customer.getContact()).length() != 10)
			throw new InvalidDataException("Contact number must contain 10 digits");

		if (customerRepository.existsByEmail(customer.getEmail()))
			throw new InvalidDataException("Email already exists");

		if (customerRepository.existsByContact(customer.getContact()))
			throw new InvalidDataException("Contact already exists");

		customer.setPassword(passwordEncoder.encode(customer.getPassword()));

		Customer saved = customerRepository.save(customer);

		res.setData(saved);
		res.setMessage("Customer Registered Successfully");
		res.setStatusCode(HttpStatus.CREATED.value());

		return res;
	}

	public ResponseStructure<Restaurant> registerRestaurant(Restaurant restaurant) {

		ResponseStructure<Restaurant> res = new ResponseStructure<>();

		if (restaurant.getName() == null || restaurant.getName().isBlank())
			throw new InvalidDataException("Restaurant name is required");

		if (restaurant.getEmail() == null || restaurant.getEmail().isBlank())
			throw new InvalidDataException("Email is required");

		if (restaurant.getPassword() == null || restaurant.getPassword().isBlank())
			throw new InvalidDataException("Password is required");

		if (restaurant.getLocation() == null || restaurant.getLocation().isBlank())
			throw new InvalidDataException("Location is required");

		if (restaurant.getRating() == null || restaurant.getRating() < 1 || restaurant.getRating() > 5)
			throw new InvalidDataException("Rating should be between 1 and 5");

		if (restaurantRepository.findByEmail(restaurant.getEmail()).isPresent())
			throw new InvalidDataException("Email already exists");

		restaurant.setPassword(passwordEncoder.encode(restaurant.getPassword()));

		Restaurant saved = restaurantRepository.save(restaurant);

		res.setData(saved);
		res.setMessage("Restaurant Registered Successfully");
		res.setStatusCode(HttpStatus.CREATED.value());

		return res;
	}

	public ResponseStructure<LoginResponse> login(String email, String password, String role) {

		ResponseStructure<LoginResponse> res = new ResponseStructure<>();

		if (email == null || email.isBlank() || password == null || password.isBlank() || role == null
				|| role.isBlank())
			throw new InvalidDataException("Email, password and role are required");

		if (role.equalsIgnoreCase("CUSTOMER")) {

			Optional<Customer> opt = customerRepository.findByEmail(email);

			if (opt.isEmpty() || opt.get().getPassword() == null
					|| !passwordEncoder.matches(password, opt.get().getPassword()))
				throw new InvalidDataException("Invalid email or password");

			Customer c = opt.get();

			res.setData(new LoginResponse("CUSTOMER", c.getCustomerId(), c.getName(), c.getEmail()));

		} else if (role.equalsIgnoreCase("RESTAURANT")) {

			Optional<Restaurant> opt = restaurantRepository.findByEmail(email);

			if (opt.isEmpty() || opt.get().getPassword() == null
					|| !passwordEncoder.matches(password, opt.get().getPassword()))
				throw new InvalidDataException("Invalid email or password");

			Restaurant r = opt.get();

			res.setData(new LoginResponse("RESTAURANT", r.getRestaurantId(), r.getName(), r.getEmail()));

		} else {
			throw new InvalidDataException("Invalid role");
		}

		res.setMessage("Login Successful");
		res.setStatusCode(HttpStatus.OK.value());

		return res;
	}

	public ResponseStructure<String> generateResetToken(String email, String role) {

		ResponseStructure<String> res = new ResponseStructure<>();

		if (email == null || email.isBlank() || role == null || role.isBlank()) {

			throw new InvalidDataException("Email and role are required");
		}

		role = role.toUpperCase();

		if (role.equals("CUSTOMER")) {

			if (customerRepository.findByEmail(email).isEmpty()) {
				throw new InvalidDataException("No customer account found with this email");
			}

		} else if (role.equals("RESTAURANT")) {

			if (restaurantRepository.findByEmail(email).isEmpty()) {
				throw new InvalidDataException("No restaurant account found with this email");
			}

		} else {

			throw new InvalidDataException("Invalid role");
		}

		PasswordResetToken resetToken = new PasswordResetToken();

		resetToken.setToken(UUID.randomUUID().toString());
		resetToken.setEmail(email);
		resetToken.setRole(role);
		resetToken.setExpiryTime(LocalDateTime.now().plusMinutes(15));
		resetToken.setUsed(false);

		passwordResetTokenRepository.save(resetToken);

		String resetLink = "https://foodhub-7zkg.onrender.com/reset-password.html?token=" + resetToken.getToken();

		emailService.sendPasswordResetEmail(email, resetLink);

		res.setData("Reset link sent to your email");
		res.setMessage("Password reset email sent successfully");
		res.setStatusCode(HttpStatus.OK.value());

		return res;
	}

	public ResponseStructure<String> resetPassword(String token, String newPassword) {

		ResponseStructure<String> res = new ResponseStructure<>();

		if (token == null || token.isBlank() || newPassword == null || newPassword.isBlank()) {

			throw new InvalidDataException("Token and new password are required");
		}

		if (newPassword.length() < 4) {

			throw new InvalidDataException("Password must contain at least 4 characters");
		}

		Optional<PasswordResetToken> opt = passwordResetTokenRepository.findByTokenAndUsedFalse(token);

		if (opt.isEmpty()) {

			throw new InvalidDataException("Invalid or already used reset token");
		}

		PasswordResetToken resetToken = opt.get();

		if (resetToken.getExpiryTime().isBefore(LocalDateTime.now())) {

			throw new InvalidDataException("Reset token has expired");
		}

		String email = resetToken.getEmail();
		String role = resetToken.getRole();

		if (role.equals("CUSTOMER")) {

			Optional<Customer> customer = customerRepository.findByEmail(email);

			if (customer.isEmpty()) {

				throw new InvalidDataException("Customer account not found");
			}

			Customer c = customer.get();

			c.setPassword(passwordEncoder.encode(newPassword));

			customerRepository.save(c);

		} else if (role.equals("RESTAURANT")) {

			Optional<Restaurant> restaurant = restaurantRepository.findByEmail(email);

			if (restaurant.isEmpty()) {

				throw new InvalidDataException("Restaurant account not found");
			}

			Restaurant r = restaurant.get();

			r.setPassword(passwordEncoder.encode(newPassword));

			restaurantRepository.save(r);

		} else {

			throw new InvalidDataException("Invalid account role");
		}

		resetToken.setUsed(true);
		passwordResetTokenRepository.save(resetToken);

		res.setData("Password reset successfully");
		res.setMessage("You can now login with your new password");
		res.setStatusCode(HttpStatus.OK.value());

		return res;
	}
}
