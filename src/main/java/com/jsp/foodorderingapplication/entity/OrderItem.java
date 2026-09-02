package com.jsp.foodorderingapplication.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer orderItemId;
	private Integer quantity;
	private Double subTotal;
	
	@ManyToOne
	@JoinColumn(name="order_id")
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private Order order;
	
	@ManyToOne
	@JoinColumn(name="menuItem_id")
	private MenuItem menuItem;
	
}
