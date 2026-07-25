package com.example.thread_co;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class OrderConsumer {

    @RabbitListener(queues = "thread_orders_queue")
    public void processOrderNotification(String orderDetails) {
        System.out.println(" 📩 [RabbitMQ Worker] New Order Received: " + orderDetails);
        
        try {
            Thread.sleep(1500); 
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println(" ✅ [RabbitMQ Worker] Email receipt sent & invoice generated for: " + orderDetails);
    }
}