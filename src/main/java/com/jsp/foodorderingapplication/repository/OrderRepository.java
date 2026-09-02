package com.jsp.foodorderingapplication.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.jsp.foodorderingapplication.Enum.OrderStatus;
import com.jsp.foodorderingapplication.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Integer> {

	List<Order> findByStatus(OrderStatus orderStatus);

	@Query("select o from Order o where date(o.orderDateTime) = :date")
	List<Order> getOrderByDate(LocalDate date);
	
	@Query("select distinct o from Order o join o.orderItems oi join oi.menuItem mi where mi.restaurant.restaurantId = :restaurantId")
	List<Order> getAllOrdersPlacedInRestaurant(Integer restaurantId);

}
