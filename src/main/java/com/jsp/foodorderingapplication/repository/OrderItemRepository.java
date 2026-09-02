package com.jsp.foodorderingapplication.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jsp.foodorderingapplication.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Integer>{

	

}
