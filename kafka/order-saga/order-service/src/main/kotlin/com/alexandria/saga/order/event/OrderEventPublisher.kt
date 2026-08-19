package com.alexandria.saga.order.event

import com.alexandria.saga.common.Topics
import com.alexandria.saga.common.event.OrderCreatedEvent
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class OrderEventPublisher(
    private val kafkaTemplate: KafkaTemplate<String, Any>,
) {
    private val logger = LoggerFactory.getLogger(OrderEventPublisher::class.java)

    fun publish(event: OrderCreatedEvent) {
        kafkaTemplate.send(Topics.ORDER_CREATED, event.orderId, event)
            .whenComplete { result, ex ->
                if (ex != null) {
                    logger.error("[order event] failed, orderId: {}", event.orderId, ex)
                } else {
                    logger.info(
                        "[order event] success, orderId: {}, partition: {}, offset: {}",
                        event.orderId,
                        result.recordMetadata.partition(),
                        result.recordMetadata.offset()
                    )

                }
            }
    }
}