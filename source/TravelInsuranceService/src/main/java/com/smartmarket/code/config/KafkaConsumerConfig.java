package com.smartmarket.code.config;

import org.apache.kafka.clients.consumer.CommitFailedException;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.TimeoutException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConsumerAwareRebalanceListener;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaListenerErrorHandler;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.time.Duration;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConsumerConfig{

    private Logger logger = LoggerFactory.getLogger(KafkaConsumerConfig.class);

    @Autowired
    ConfigurableEnvironment environment;

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
//        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092");
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, environment.getRequiredProperty("kafka.bootrapServer"));
//        props.put(ConsumerConfig.GROUP_ID_CONFIG, environment.getRequiredProperty("kafka.groupID"));
//        props.put(ConsumerConfig.CLIENT_ID_CONFIG, "consumer-group_id-1");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
//        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG,100);
//        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG,900000);
//        props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG,1000);
//        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG,600000);
//        props.put(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG,300000);
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
//            @Override
//            public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
//                // acknowledge any pending Acknowledgments (if using manual acks)
//                // commit before rebalance
////                acknowledgment.acknowledge();
//            }

            //Auto triggered before rebalancing starts and after the consumer stopped consuming messages
            @Override
            public void onPartitionsRevokedBeforeCommit(Consumer<?, ?> consumer, Collection<TopicPartition> partitions) {
                // acknowledge any pending Acknowledgments (if using manual acks)
                // commit before rebalance
                consumer.commitSync();
            }

            //Auto triggered after rebalancing stop but before consumer starts consuming messages
            @Override
            public void onPartitionsAssigned(Consumer<?, ?> consumer,Collection<TopicPartition> partitions) {

            }
        });

        //exception at the container level
        factory.setBatchErrorHandler(((exception, data) -> {
         /* here you can do you custom handling, I am just logging it same as default Error handler does
        If you just want to log. you need not configure the error handler here. The default handler does it for you.
         Generally, you will persist the failed records to DB for tracking the failed records.  */
            logger.error("Error in process with Exception {} and the record is {}", exception, data);
        }));

//        2 thread to listen message (~ 2 thread of consumer)
//        factory.setConcurrency(2);
        return factory;
    }

    @Bean("customErrorHandler")
    public KafkaListenerErrorHandler listenerErrorHandler() {
        return (m, e) -> {
            logger.error("error-kafka");
            return m;
        };
    }
}