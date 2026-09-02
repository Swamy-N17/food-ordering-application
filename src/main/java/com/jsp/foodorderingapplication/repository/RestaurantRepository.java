package com.jsp.foodorderingapplication.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jsp.foodorderingapplication.entity.Restaurant;

public interface RestaurantRepository extends JpaRepository<Restaurant, Integer> {





List<Restaurant> findRestaurantByName(String name);

	List<Restaurant> findRestaurantByLocation(String location);

	List<Restaurant> findRestaurantsByRatingGreaterThan(Integer rating);

	Optional<Restaurant> findByEmail(String email);
}
