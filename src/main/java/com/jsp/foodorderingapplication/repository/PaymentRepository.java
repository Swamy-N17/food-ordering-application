package com.jsp.foodorderingapplication.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jsp.foodorderingapplication.Enum.PaymentMethod;
import com.jsp.foodorderingapplication.Enum.PaymentStatus;
import com.jsp.foodorderingapplication.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {

	List<Payment> findByPaymentStatus(PaymentStatus paymentSt);

	List<Payment> findByPaymentMethod(PaymentMethod paymentMethod);

}
