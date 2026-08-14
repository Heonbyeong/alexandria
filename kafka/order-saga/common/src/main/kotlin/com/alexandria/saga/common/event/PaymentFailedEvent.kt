package com.alexandria.saga.common.event

import java.time.Instant

data class PaymentFailedEvent(
    val orderId: String,
    val reason: String,
    val occurredAt: Instant = Instant.now(),
)
