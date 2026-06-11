package com.technokratos.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class TestContainersConfig {

    @Bean
    public KafkaContainer kafkaContainer(DynamicPropertyRegistry registry) {
        KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.5.0"));
        kafka.start();

        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);

        return kafka;
    }

    @Bean
    public MongoDBContainer mongoContainer(DynamicPropertyRegistry registry) {
        MongoDBContainer mongo = new MongoDBContainer(DockerImageName.parse("mongo:4.4"))
                .withCommand("--bind_ip_all");
        mongo.start();

        registry.add("spring.data.mongodb.uri", mongo::getReplicaSetUrl);
        registry.add("MONGO_HOST", mongo::getHost);
        registry.add("MONGO_PORT", () -> String.valueOf(mongo.getMappedPort(27017)));
        registry.add("SPRING_DATA_MONGODB_URI", mongo::getReplicaSetUrl);

        return mongo;
    }
}
