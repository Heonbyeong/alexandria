package com.alexandria.saga.common.event

import java.time.Instant

data class OrderCreatedEvent(
    val orderId: String,
    val userId: String,
    val amount: Long,
    val occurredAt: Instant = Instant.now(),
)
