package com.alexandria.saga.order.domain

import java.time.LocalDateTime

data class Order(
    val id: String,
    val userId: String,
    val amount: Long,
    var status: OrderStatus = OrderStatus.PENDING,
    val createdAt: LocalDateTime = LocalDateTime.now(),
)