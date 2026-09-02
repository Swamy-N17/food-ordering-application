package com.jsp.foodorderingapplication.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jsp.foodorderingapplication.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {

	boolean existsByEmail(String email);

	boolean existsByContact(Long contact);

	Optional<Customer> findByContact(Long contact);

	Optional<Customer> findByEmail(String email);

	List<Customer> findByName(String name);
}
