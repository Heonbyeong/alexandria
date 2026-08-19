package com.alexandria.saga.order

import com.alexandria.saga.common.Topics
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.TopicBuilder

@Configuration
class KafkaConfig {
    @Bean
    fun orderCreatedTopic() =
        TopicBuilder
            .name(Topics.ORDER_CREATED)
            .partitions(3)
            .replicas(1)
            .build()

    @Bean
    fun paymentCompletedTopic() =
        TopicBuilder
            .name(Topics.PAYMENT_COMPLETED)
            .partitions(3)
            .replicas(1)
            .build()

    @Bean
    fun paymentFailedTopic() =
        TopicBuilder
            .name(Topics.PAYMENT_FAILED)
            .partitions(3)
            .replicas(1)
            .build()
}