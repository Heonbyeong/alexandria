package com.alexandria.saga.order.api

import com.alexandria.saga.order.api.dto.CreateOrderRequest
import com.alexandria.saga.order.domain.Order
import com.alexandria.saga.order.service.OrderService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/orders")
class OrderController(
    private val orderService: OrderService,
) {
    @PostMapping
    fun create(@RequestBody request: CreateOrderRequest): Order =
        orderService.create(request.userId, request.amount)

    @GetMapping("/{id}")
    fun get(@PathVariable id: String): ResponseEntity<Order> =
        orderService.getById(id)
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()
}
