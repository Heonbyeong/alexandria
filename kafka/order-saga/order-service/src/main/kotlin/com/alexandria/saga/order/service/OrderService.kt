package com.alexandria.saga.order.service

import com.alexandria.saga.common.event.OrderCreatedEvent
import com.alexandria.saga.order.domain.Order
import com.alexandria.saga.order.domain.repository.OrderRepository
import com.alexandria.saga.order.event.OrderEventPublisher
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val orderEventPublisher: OrderEventPublisher
) {
    fun create(userId: String, amount: Long): Order {
        val order = Order(
            id = UUID.randomUUID().toString(),
            userId = userId,
            amount = amount,
        )
        val savedOrder = orderRepository.save(order)
        val orderCreatedEvent = OrderCreatedEvent(
            orderId = savedOrder.id,
            userId = savedOrder.userId,
            amount = savedOrder.amount,
        )

        orderEventPublisher.publish(orderCreatedEvent)

        return savedOrder
    }

    fun getById(id: String): Order? = orderRepository.findById(id)
}