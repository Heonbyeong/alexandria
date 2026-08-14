package com.alexandria.saga.common.event

import java.time.Instant

data class PaymentCompletedEvent(
    val orderId: String,
    val paymentId: String,
    val amount: Long,
    val occurredAt: Instant = Instant.now(),
)
