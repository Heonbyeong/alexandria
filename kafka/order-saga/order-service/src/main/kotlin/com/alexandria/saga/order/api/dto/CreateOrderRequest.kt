package com.alexandria.saga.order.api.dto

data class CreateOrderRequest(
    val userId: String,
    val amount: Long,
)
