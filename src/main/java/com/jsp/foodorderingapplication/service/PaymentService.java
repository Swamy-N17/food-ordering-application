package com.jsp.foodorderingapplication.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.jsp.foodorderingapplication.Enum.PaymentMethod;
import com.jsp.foodorderingapplication.Enum.PaymentStatus;
import com.jsp.foodorderingapplication.dto.ResponseStructure;
import com.jsp.foodorderingapplication.entity.Order;
import com.jsp.foodorderingapplication.entity.Payment;
import com.jsp.foodorderingapplication.exception.IdNotFoundException;
import com.jsp.foodorderingapplication.exception.InvalidDataException;
import com.jsp.foodorderingapplication.exception.InvalidFieldException;
import com.jsp.foodorderingapplication.repository.OrderRepository;
import com.jsp.foodorderingapplication.repository.PaymentRepository;

@Service
public class PaymentService {
	@Autowired
	private PaymentRepository paymentRepository;

	@Autowired
	private OrderRepository orderRepository;

	public ResponseStructure<Payment> getPaymentById(Integer id) {

		ResponseStructure<Payment> res = new ResponseStructure<Payment>();

		Optional<Payment> opt = paymentRepository.findById(id);

		if (opt.isPresent()) {

			res.setData(opt.get());
			res.setMessage("Payment With ID: " + id + " Fetched Successfully");
			res.setStatusCode(HttpStatus.OK.value());

			return res;
		}

		else
			throw new IdNotFoundException("Payment With ID: " + id + " Not Found");
	}

	public ResponseStructure<Payment> getPaymentByOrder(Integer orderId) {

		ResponseStructure<Payment> res = new ResponseStructure<>();

		Optional<Order> optOrder = orderRepository.findById(orderId);

		if (optOrder.isPresent()) {

			Payment payment = optOrder.get().getPayment();

			if (payment != null) {

				res.setData(payment);
				res.setMessage("Payment with Order Id: " + orderId + " Fetched Successfully");
				res.setStatusCode(HttpStatus.OK.value());

				return res;
			}

			else
				throw new IdNotFoundException("Payment Not Found For Order With ID: " + orderId);
		}

		else
			throw new IdNotFoundException("Order With ID: " + orderId + " Not Found");
	}

	public ResponseStructure<List<Payment>> getPaymentByStatus(String paymentStatus) {

		ResponseStructure<List<Payment>> res = new ResponseStructure<List<Payment>>();

		if (!(paymentStatus.equals("PENDING") || paymentStatus.equals("SUCCESS") || paymentStatus.equals("FAILED")
				|| paymentStatus.equals("REFUNDED"))) {

			throw new InvalidDataException("Invalid Payment Status: " + paymentStatus);
		}

		PaymentStatus paymentSt = PaymentStatus.valueOf(paymentStatus);

		List<Payment> payments = paymentRepository.findByPaymentStatus(paymentSt);

		if (payments.isEmpty())
			throw new IdNotFoundException("No Orders Found With Status: " + paymentStatus);

		res.setData(payments);
		res.setMessage("Payment With Status " + paymentStatus + " Fetched Successfully");
		res.setStatusCode(HttpStatus.OK.value());

		return res;
	}
	
	public ResponseStructure<List<Payment>> getPaymentByMethod(String paymentMethod) {
		
		ResponseStructure<List<Payment>> res = new ResponseStructure<List<Payment>>();
		
		if (!(paymentMethod.equals("CASH") || paymentMethod.equals("UPI") || paymentMethod.equals("CREDIT_CARD")
				|| paymentMethod.equals("DEBIT_CARD"))) {
			
			throw new InvalidDataException("Invalid Payment Method: " + paymentMethod);
		}
		
		PaymentMethod paymentMethodType = PaymentMethod.valueOf(paymentMethod);
		
		List<Payment> payments = paymentRepository.findByPaymentMethod(paymentMethodType);
		
		if (payments.isEmpty())
			throw new IdNotFoundException("No Payment Found With Method Type: " + paymentMethod);
		
		res.setData(payments);
		res.setMessage("Payment With Method Type " + paymentMethod + " Fetched Successfully");
		res.setStatusCode(HttpStatus.OK.value());
		
		return res;
	}
	
	
	
	public ResponseStructure<String> updatePaymentStatus(
	        Map<String, Object> data, Integer paymentId) {

	    ResponseStructure<String> res = new ResponseStructure<String>();

	    // Check payment exists
	    Optional<Payment> opt = paymentRepository.findById(paymentId);

	    if (opt.isEmpty()) {
	        throw new IdNotFoundException(
	                "Payment With ID: " + paymentId + " Not Found");
	    }

	    Payment payment = opt.get();

	    // Check request field
	    if (!data.containsKey("paymentStatus")) {
	        throw new InvalidFieldException(
	                "Request must contain paymentStatus");
	    }

	    String paymentStatus = (String) data.get("paymentStatus");

	    // Validate payment status
	    if (!(paymentStatus.equals("PENDING")
	            || paymentStatus.equals("SUCCESS")
	            || paymentStatus.equals("FAILED")
	            || paymentStatus.equals("REFUNDED"))) {

	        throw new InvalidDataException(
	                "Invalid Payment Status: " + paymentStatus);
	    }

	    // Update payment status
	    payment.setPaymentStatus(
	            PaymentStatus.valueOf(paymentStatus));

	    // Save updated payment
	    paymentRepository.save(payment);

	    res.setData("Success");
	    res.setMessage("Payment Status Updated Successfully");
	    res.setStatusCode(HttpStatus.OK.value());

	    return res;
	}
}