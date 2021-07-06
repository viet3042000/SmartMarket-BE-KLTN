package com.smartmarket.code.config;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConsumerAwareRebalanceListener;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.Acknowledgment;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConsumerConfig{

//    @Value("${test-kafka-bootrapServer}")
//    String bootrapServer;
//
//    @Value("${test-kafka-groupID}")
//    String groupID;

    @Autowired
    ConfigurableEnvironment environment;

    @Autowired
    Acknowledgment acknowledgment;

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
//        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092");
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, environment.getRequiredProperty("kafka.bootrapServer"));
//        props.put(ConsumerConfig.GROUP_ID_CONFIG, "group_id");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, environment.getRequiredProperty("kafka.groupID"));
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, "consumer-group_id-1");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG,100);
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG,900000);
        props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG,1000);
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG,600000);
        props.put(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG,300000);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(props);
    }


    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String>
                factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
//        factory.getContainerProperties().setCommitRetries(2);
        factory.setBatchListener(true);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
//        factory.getContainerProperties().setSyncCommits(false);


//        factory.getContainerProperties().setConsumerRebalanceListener(new ConsumerAwareRebalanceListener() {
        factory.getContainerProperties().setConsumerRebalanceListener(new ConsumerAwareRebalanceListener() {

            //Auto triggered before rebalancing starts and after the consumer stopped consuming messages
            @Override
            public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
                // acknowledge any pending Acknowledgments (if using manual acks)
                // commit before rebalance
//                acknowledgment.acknowledge();
            }

            //Auto triggered after rebalancing stop but before consumer starts consuming messages
            @Override
            public void onPartitionsAssigned(Consumer<?, ?> consumer,Collection<TopicPartition> partitions) {

            }
        });


        //2 thread to listen message (~ 2 thread of consumer)
//        factory.setConcurrency(2);
        return factory;
    }
}