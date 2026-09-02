package com.jsp.foodorderingapplication.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jsp.foodorderingapplication.Enum.OrderStatus;
import com.jsp.foodorderingapplication.Enum.PaymentMethod;
import com.jsp.foodorderingapplication.Enum.PaymentStatus;
import com.jsp.foodorderingapplication.dto.ResponseStructure;
import com.jsp.foodorderingapplication.entity.Customer;
import com.jsp.foodorderingapplication.entity.MenuItem;
import com.jsp.foodorderingapplication.entity.Order;
import com.jsp.foodorderingapplication.entity.OrderItem;
import com.jsp.foodorderingapplication.entity.Payment;
import com.jsp.foodorderingapplication.entity.Restaurant;
import com.jsp.foodorderingapplication.exception.IdNotFoundException;
import com.jsp.foodorderingapplication.exception.InvalidDataException;
import com.jsp.foodorderingapplication.exception.InvalidFieldException;
import com.jsp.foodorderingapplication.repository.CustomerRepository;
import com.jsp.foodorderingapplication.repository.MenuItemRespository;
import com.jsp.foodorderingapplication.repository.OrderRepository;
import com.jsp.foodorderingapplication.repository.PaymentRepository;
import com.jsp.foodorderingapplication.repository.RestaurantRepository;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private MenuItemRespository menuItemRespository;

    @Autowired
    private PaymentRepository paymentRepository;

    public ResponseStructure<Order> placeOrder(Order order) {

        ResponseStructure<Order> res = new ResponseStructure<>();

        if (order == null)
            throw new InvalidDataException("Order details are required");

        if (order.getCustomer() == null
                || order.getCustomer().getCustomerId() == null)
            throw new InvalidDataException("Customer is required");

        if (order.getOrderItems() == null
                || order.getOrderItems().isEmpty())
            throw new InvalidDataException("Order must have at least one item");

        if (order.getPayment() == null)
            throw new InvalidDataException("Payment details are required");

        if (order.getPayment().getPaymentMethod() == null)
            throw new InvalidDataException("Payment method is required");

        Optional<Customer> optCustomer =
                customerRepository.findById(
                        order.getCustomer().getCustomerId());

        if (optCustomer.isEmpty())
            throw new IdNotFoundException(
                    "Customer with the given ID does not exist.");

        order.setCustomer(optCustomer.get());

        double totalAmount = 0.0;

        for (OrderItem orderItem : order.getOrderItems()) {

            if (orderItem.getMenuItem() == null
                    || orderItem.getMenuItem().getMenuItemId() == null)
                throw new InvalidDataException("Menu Item is required");

            if (orderItem.getQuantity() == null
                    || orderItem.getQuantity() <= 0)
                throw new InvalidDataException(
                        "Order Item Must Contain Minimum One Quantity");

            Optional<MenuItem> optMenuItem =
                    menuItemRespository.findById(
                            orderItem.getMenuItem().getMenuItemId());

            if (optMenuItem.isEmpty())
                throw new IdNotFoundException(
                        "Menu Item With ID: "
                                + orderItem.getMenuItem().getMenuItemId()
                                + " Not Found");

            MenuItem menuItem = optMenuItem.get();

            if (!Boolean.TRUE.equals(menuItem.getAvailability()))
                throw new InvalidDataException(
                        "Menu Item is Unavailable");

            orderItem.setMenuItem(menuItem);
            orderItem.setOrder(order);

            double subTotal =
                    menuItem.getPrice() * orderItem.getQuantity();

            orderItem.setSubTotal(subTotal);
            totalAmount += subTotal;
        }

        order.setTotalAmount(totalAmount);
        // Store the delivery address used for this order.
        // This keeps the order address even if the customer changes their profile later.
        order.setDeliveryAddress(optCustomer.get().getAddress());

        if (order.getPayment().getAmount() == null
                || Math.abs(order.getPayment().getAmount() - totalAmount) > 0.001)
            throw new InvalidDataException(
                    "Payment amount does not match the order total amount");

        order.getPayment().setOrder(order);

        // Online payments are treated as successful in this demo project.
        // Cash on delivery remains pending until handled by the restaurant.
        if (order.getPayment().getPaymentStatus() == null) {
            if (order.getPayment().getPaymentMethod() == PaymentMethod.CASH)
                order.getPayment().setPaymentStatus(PaymentStatus.PENDING);
            else
                order.getPayment().setPaymentStatus(PaymentStatus.SUCCESS);
        }

        order.setStatus(OrderStatus.PLACED);
        order.setOrderDateTime(LocalDateTime.now());

        res.setData(orderRepository.save(order));
        res.setMessage("Order Successfully Placed");
        res.setStatusCode(HttpStatus.CREATED.value());

        return res;
    }

    public ResponseStructure<List<Order>> getAllOrders() {

        ResponseStructure<List<Order>> res = new ResponseStructure<>();
        List<Order> orders = orderRepository.findAll();

        if (orders.isEmpty())
            throw new IdNotFoundException("No Orders Found");

        res.setData(orders);
        res.setMessage("All Order Records Fetched Successfully");
        res.setStatusCode(HttpStatus.OK.value());

        return res;
    }

    public ResponseStructure<List<Order>> getAllOrdersOfCustomer(
            Integer customerId) {

        ResponseStructure<List<Order>> res = new ResponseStructure<>();

        Optional<Customer> opt = customerRepository.findById(customerId);

        if (opt.isEmpty())
            throw new IdNotFoundException(
                    "Customer With ID: " + customerId + " Not Found");

        List<Order> orders = opt.get().getOrders();

        if (orders == null || orders.isEmpty())
            throw new IdNotFoundException(
                    "No Orders Found for Customer With ID: " + customerId);

        res.setData(orders);
        res.setMessage(
                "All Orders of Customer With ID: "
                        + customerId + " Fetched Successfully");
        res.setStatusCode(HttpStatus.OK.value());

        return res;
    }

    public ResponseStructure<Order> getOrderById(Integer id) {

        ResponseStructure<Order> res = new ResponseStructure<>();

        Optional<Order> opt = orderRepository.findById(id);

        if (opt.isEmpty())
            throw new IdNotFoundException(
                    "Order With ID: " + id + " Not Found");

        res.setData(opt.get());
        res.setMessage(
                "Order With ID: " + id + " Fetched Successfully");
        res.setStatusCode(HttpStatus.OK.value());

        return res;
    }

    public ResponseStructure<String> updateOrderStatus(
            Map<String, Object> data, Integer id) {

        ResponseStructure<String> res = new ResponseStructure<>();

        Optional<Order> opt = orderRepository.findById(id);

        if (opt.isEmpty())
            throw new IdNotFoundException(
                    "Order With ID: " + id + " Not Found");

        if (data == null || !data.containsKey("status"))
            throw new InvalidFieldException(
                    "Request must contain status");

        String status = String.valueOf(data.get("status"));

        try {
            OrderStatus orderStatus = OrderStatus.valueOf(status);
            opt.get().setStatus(orderStatus);
        } catch (IllegalArgumentException e) {
            throw new InvalidDataException(
                    "Invalid Order Status: " + status);
        }

        orderRepository.save(opt.get());

        res.setData("Success");
        res.setMessage("Order Status Updated Successfully");
        res.setStatusCode(HttpStatus.OK.value());

        return res;
    }

    @Transactional
    public ResponseStructure<String> cancelOrder(Integer id) {

        ResponseStructure<String> res = new ResponseStructure<>();

        Optional<Order> opt = orderRepository.findById(id);

        if (opt.isEmpty())
            throw new IdNotFoundException(
                    "Order With ID: " + id + " Not Found");

        Order order = opt.get();

        if (order.getStatus() == OrderStatus.PREPARED
                || order.getStatus() == OrderStatus.OUT_FOR_DELIVERY
                || order.getStatus() == OrderStatus.DELIVERED)
            throw new InvalidDataException(
                    "Order Cannot Be Cancelled at this Stage");

        order.setStatus(OrderStatus.CANCELLED);

        // A successfully paid order is refunded when it is cancelled.
        Payment payment = order.getPayment();
        if (payment != null && payment.getPaymentStatus() == PaymentStatus.SUCCESS) {
            payment.setPaymentStatus(PaymentStatus.REFUNDED);
            paymentRepository.save(payment);
        }

        orderRepository.save(order);

        res.setData("Success");
        res.setMessage("Order Cancelled Successfully");
        res.setStatusCode(HttpStatus.OK.value());

        return res;
    }

    public ResponseStructure<List<Order>> getOrdersByStatus(String status) {

        ResponseStructure<List<Order>> res = new ResponseStructure<>();

        try {
            OrderStatus orderStatus = OrderStatus.valueOf(status);
            List<Order> orders =
                    orderRepository.findByStatus(orderStatus);

            if (orders.isEmpty())
                throw new IdNotFoundException(
                        "No Orders Found With Status: " + status);

            res.setData(orders);
            res.setMessage(
                    "Orders With Status " + status
                            + " Fetched Successfully");
            res.setStatusCode(HttpStatus.OK.value());
            return res;

        } catch (IllegalArgumentException e) {
            throw new InvalidDataException(
                    "Invalid Order Status: " + status);
        }
    }

    public ResponseStructure<List<Order>> getOrdersByDate(
            LocalDate date) {

        ResponseStructure<List<Order>> res = new ResponseStructure<>();

        List<Order> orders = orderRepository.getOrderByDate(date);

        if (orders.isEmpty())
            throw new IdNotFoundException(
                    "No Orders Found On Date: " + date);

        res.setData(orders);
        res.setMessage(
                "Orders On Date: " + date + " Fetched Successfully");
        res.setStatusCode(HttpStatus.OK.value());

        return res;
    }

    public ResponseStructure<List<Order>> getAllOrdersPlacedInRestaurant(
            Integer restaurantId) {

        ResponseStructure<List<Order>> res = new ResponseStructure<>();

        Optional<Restaurant> opt =
                restaurantRepository.findById(restaurantId);

        if (opt.isEmpty())
            throw new IdNotFoundException(
                    "Restaurant With ID: " + restaurantId + " Not Found");

        List<Order> orders =
                orderRepository.getAllOrdersPlacedInRestaurant(restaurantId);

        if (orders.isEmpty())
            throw new IdNotFoundException(
                    "No Orders Found in Restaurant With ID: "
                            + restaurantId);

        res.setData(orders);
        res.setMessage(
                "All Orders Placed in Restaurant With ID: "
                        + restaurantId + " Fetched Successfully");
        res.setStatusCode(HttpStatus.OK.value());

        return res;
    }
}
