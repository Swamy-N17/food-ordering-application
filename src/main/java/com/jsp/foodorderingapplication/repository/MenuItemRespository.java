package com.jsp.foodorderingapplication.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jsp.foodorderingapplication.entity.MenuItem;

public interface MenuItemRespository extends JpaRepository<MenuItem, Integer> {

	List<MenuItem> findByItemName(String itemName);

	boolean existsByItemNameAndRestaurant_RestaurantId(
			String itemName,
			Integer restaurantId
	);

}