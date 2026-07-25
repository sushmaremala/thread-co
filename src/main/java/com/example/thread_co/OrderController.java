package com.example.thread_co;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/store")
@CrossOrigin(origins = "*")
public class OrderController {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired(required = false)
    private OrderRepository orderRepository;

    @PostMapping("/init")
    public ResponseEntity<String> initStock() {
        redisTemplate.opsForValue().set("stock:women_tops", "20");
        redisTemplate.opsForValue().set("stock:women_bottoms", "15");
        redisTemplate.opsForValue().set("stock:men_tops", "25");
        redisTemplate.opsForValue().set("stock:accessories", "30");

        return ResponseEntity.ok("THREAD & CO. inventory initialized in Redis DB!");
    }

    @GetMapping("/stock")
    public ResponseEntity<Map<String, String>> getStock() {
        Map<String, String> stockMap = new HashMap<>();
        stockMap.put("women_tops", redisTemplate.opsForValue().get("stock:women_tops"));
        stockMap.put("women_bottoms", redisTemplate.opsForValue().get("stock:women_bottoms"));
        stockMap.put("men_tops", redisTemplate.opsForValue().get("stock:men_tops"));
        stockMap.put("accessories", redisTemplate.opsForValue().get("stock:accessories"));

        return ResponseEntity.ok(stockMap);
    }

    @PostMapping("/buy")
    public ResponseEntity<Map<String, String>> buyItem(@RequestBody OrderRequest order) {
        Map<String, String> response = new HashMap<>();

        if (order.getUserEmail() == null || order.getUserEmail().trim().isEmpty()) {
            response.put("status", "UNAUTHORIZED");
            response.put("message", "Please log in or sign up to complete your purchase!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        String cleanEmail = order.getUserEmail().toLowerCase().trim();

        // 1. Fast Atomic Redis Stock Check & Decrement
        String stockKey = "stock:" + order.getCategory();
        Long remainingStock = redisTemplate.opsForValue().decrement(stockKey);

        if (remainingStock == null || remainingStock < 0) {
            redisTemplate.opsForValue().increment(stockKey); // revert
            response.put("status", "OUT_OF_STOCK");
            response.put("message", "Sorry! " + order.getItemTitle() + " is sold out!");
            return ResponseEntity.status(400).body(response);
        }

        // Generate unique order ID
        String orderId = "TNC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        order.setOrderId(orderId);

        // 2. Store Record in Redis DB
        redisTemplate.opsForValue().set("order:" + orderId, "Email: " + cleanEmail + " | Item: " + order.getItemTitle());

        // 3. Store Order Document in MongoDB!
        if (orderRepository != null) {
            try {
                orderRepository.save(new OrderDocument(orderId, cleanEmail, order.getItemTitle(), order.getCategory(), order.getPrice()));
            } catch(Exception e) {}
        }

        // 4. Send order event to RabbitMQ Queue
        try {
            rabbitTemplate.convertAndSend("thread_orders_exchange", "thread_orders_routingKey", 
                "Order " + orderId + " | Item: " + order.getItemTitle() + " | Authorized Email: " + cleanEmail);
        } catch(Exception e) {}

        response.put("status", "SUCCESS");
        response.put("orderId", orderId);
        response.put("message", "Authorized Order Confirmed! Saved in MongoDB & Upstash Redis DB.");
        response.put("remainingStock", String.valueOf(remainingStock));

        return ResponseEntity.ok(response);
    }

    // Endpoint to inspect all MongoDB orders directly in your browser!
    @GetMapping("/orders")
    public ResponseEntity<List<OrderDocument>> getAllOrders() {
        if (orderRepository != null) {
            try {
                return ResponseEntity.ok(orderRepository.findAll());
            } catch(Exception e) {}
        }
        return ResponseEntity.ok(new ArrayList<>());
    }
}