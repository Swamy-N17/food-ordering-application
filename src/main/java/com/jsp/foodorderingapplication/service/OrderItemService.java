package com.jsp.foodorderingapplication.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.jsp.foodorderingapplication.Enum.OrderStatus;
import com.jsp.foodorderingapplication.dto.ResponseStructure;
import com.jsp.foodorderingapplication.entity.MenuItem;
import com.jsp.foodorderingapplication.entity.Order;
import com.jsp.foodorderingapplication.entity.OrderItem;
import com.jsp.foodorderingapplication.exception.IdNotFoundException;
import com.jsp.foodorderingapplication.exception.InvalidDataException;
import com.jsp.foodorderingapplication.exception.InvalidFieldException;
import com.jsp.foodorderingapplication.repository.MenuItemRespository;
import com.jsp.foodorderingapplication.repository.OrderItemRepository;
import com.jsp.foodorderingapplication.repository.OrderRepository;

@Service
public class OrderItemService {

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MenuItemRespository menuItemRespository;

    public ResponseStructure<OrderItem> createOrderItem(OrderItem orderItem) {

        ResponseStructure<OrderItem> res = new ResponseStructure<>();

        if (orderItem == null || orderItem.getOrder() == null
                || orderItem.getOrder().getOrderId() == null)
            throw new InvalidDataException("Order is required for OrderItem");

        if (orderItem.getMenuItem() == null
                || orderItem.getMenuItem().getMenuItemId() == null)
            throw new InvalidDataException("Menu Item is required for OrderItem");

        if (orderItem.getQuantity() == null || orderItem.getQuantity() <= 0)
            throw new InvalidDataException(
                    "Order Item Must Contain Minimum One Quantity");

        Optional<Order> optOrder =
                orderRepository.findById(orderItem.getOrder().getOrderId());

        if (optOrder.isEmpty())
            throw new IdNotFoundException("Order not found for OrderItem");

        Order order = optOrder.get();

        if (order.getOrderItems() == null)
            throw new InvalidDataException("Order items are not available");

        if (order.getStatus() == OrderStatus.OUT_FOR_DELIVERY
                || order.getStatus() == OrderStatus.DELIVERED
                || order.getStatus() == OrderStatus.CANCELLED)
            throw new InvalidDataException("Item Cannot Be Added At This Stage");

        Optional<MenuItem> optMenuItem =
                menuItemRespository.findById(
                        orderItem.getMenuItem().getMenuItemId());

        if (optMenuItem.isEmpty())
            throw new IdNotFoundException("Menu Item not found for OrderItem");

        MenuItem menuItem = optMenuItem.get();

        if (!Boolean.TRUE.equals(menuItem.getAvailability()))
            throw new InvalidDataException("Ordered Item is unavailable");

        orderItem.setOrder(order);
        orderItem.setMenuItem(menuItem);
        orderItem.setSubTotal(orderItem.getQuantity() * menuItem.getPrice());

        order.getOrderItems().add(orderItem);

        double totalAmount = 0.0;
        for (OrderItem item : order.getOrderItems()) {
            totalAmount += item.getSubTotal();
        }

        order.setTotalAmount(totalAmount);

        if (order.getPayment() != null) {
            order.getPayment().setAmount(totalAmount);
        }

        orderItemRepository.save(orderItem);
        orderRepository.save(order);

        res.setData(orderItem);
        res.setMessage(
                "Order Item Saved Successfully with ID: "
                        + orderItem.getOrderItemId());
        res.setStatusCode(HttpStatus.CREATED.value());

        return res;
    }

    public ResponseStructure<String> updateItemQuantity(
            Map<String, Object> data, Integer id) {

        ResponseStructure<String> res = new ResponseStructure<>();

        Optional<OrderItem> opt = orderItemRepository.findById(id);

        if (opt.isEmpty())
            throw new IdNotFoundException(
                    "Order Item With ID: " + id + " Not Found");

        OrderItem orderItem = opt.get();

        if (orderItem.getOrder() == null)
            throw new InvalidDataException("Order not found for OrderItem");

        if (data == null || !data.containsKey("quantity"))
            throw new InvalidFieldException(
                    "Request must contain quantity");

        Object value = data.get("quantity");

        if (!(value instanceof Number))
            throw new InvalidDataException("Quantity must be a number");

        int quantity = ((Number) value).intValue();

        if (quantity <= 0)
            throw new InvalidDataException(
                    "Order Item Must Contain Minimum One Quantity");

        Order order = orderItem.getOrder();

        if (order.getStatus() == OrderStatus.OUT_FOR_DELIVERY
                || order.getStatus() == OrderStatus.DELIVERED
                || order.getStatus() == OrderStatus.CANCELLED)
            throw new InvalidDataException(
                    "Quantity Cannot Be Updated At This Stage");

        orderItem.setQuantity(quantity);
        orderItem.setSubTotal(
                quantity * orderItem.getMenuItem().getPrice());

        double totalAmount = 0.0;

        for (OrderItem item : order.getOrderItems()) {
            if (item.getOrderItemId().equals(orderItem.getOrderItemId()))
                totalAmount += orderItem.getSubTotal();
            else
                totalAmount += item.getSubTotal();
        }

        order.setTotalAmount(totalAmount);

        if (order.getPayment() != null) {
            order.getPayment().setAmount(totalAmount);
        }

        orderItemRepository.save(orderItem);
        orderRepository.save(order);

        res.setData("Success");
        res.setMessage("Order Item Quantity Updated Successfully");
        res.setStatusCode(HttpStatus.OK.value());

        return res;
    }

    public ResponseStructure<String> removeItemFromOrder(Integer orderItemId) {

        ResponseStructure<String> res = new ResponseStructure<>();

        Optional<OrderItem> opt = orderItemRepository.findById(orderItemId);

        if (opt.isEmpty())
            throw new IdNotFoundException(
                    "Order Item With ID: " + orderItemId + " Not Found");

        OrderItem orderItem = opt.get();
        Order order = orderItem.getOrder();

        if (order == null)
            throw new IdNotFoundException(
                    "Order not found for Order Item With ID: "
                            + orderItemId);

        if (order.getOrderItems().size() == 1)
            throw new InvalidDataException(
                    "Order Must Contain At Least One Order Item");

        if (order.getStatus() == OrderStatus.OUT_FOR_DELIVERY
                || order.getStatus() == OrderStatus.DELIVERED
                || order.getStatus() == OrderStatus.CANCELLED)
            throw new InvalidDataException(
                    "Item Cannot Be Removed At This Stage");

        order.getOrderItems().remove(orderItem);

        double totalAmount = 0.0;

        for (OrderItem item : order.getOrderItems()) {
            totalAmount += item.getSubTotal();
        }

        order.setTotalAmount(totalAmount);

        if (order.getPayment() != null) {
            order.getPayment().setAmount(totalAmount);
        }

        orderRepository.save(order);
        orderItemRepository.delete(orderItem);

        res.setData("Success");
        res.setMessage("Order Item Removed Successfully");
        res.setStatusCode(HttpStatus.OK.value());

        return res;
    }

    public ResponseStructure<List<OrderItem>> getAllOrderItemsOfOrder(
            Integer orderId) {

        ResponseStructure<List<OrderItem>> res = new ResponseStructure<>();

        Optional<Order> optOrder = orderRepository.findById(orderId);

        if (optOrder.isEmpty())
            throw new IdNotFoundException(
                    "Order With ID: " + orderId + " Not Found");

        List<OrderItem> orderItems = optOrder.get().getOrderItems();

        if (orderItems == null || orderItems.isEmpty())
            throw new IdNotFoundException(
                    "No Order Items Found For Order With ID: " + orderId);

        res.setData(orderItems);
        res.setMessage(
                "All Order Items For Order With ID: "
                        + orderId + " Fetched Successfully");
        res.setStatusCode(HttpStatus.OK.value());

        return res;
    }
}
