package com.alexandria.saga.order.domain.repository

import com.alexandria.saga.order.domain.Order
import org.springframework.stereotype.Repository
import java.util.concurrent.ConcurrentHashMap

@Repository
class OrderRepository {
    private val store = ConcurrentHashMap<String, Order>()

    fun save(order: Order): Order {
        store[order.id] = order

        return order
    }

    fun findById(id: String): Order? {
        return store[id]
    }

    fun findAll(): List<Order> {
        return store.values.toList()
    }
}