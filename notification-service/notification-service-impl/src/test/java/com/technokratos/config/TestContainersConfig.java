package com.technokratos.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class TestContainersConfig {

    @Bean
    @ServiceConnection
    public KafkaContainer kafkaContainer() {
        KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.5.0"));

        kafka.start();
        System.setProperty("spring.kafka.bootstrap-servers", kafka.getBootstrapServers());

        return kafka;
    }

    @Bean
    @ServiceConnection
    public MongoDBContainer mongoContainer() {
        MongoDBContainer mongo = new MongoDBContainer(DockerImageName.parse("mongo:4.4"))
                .withCommand("--bind_ip_all");

        mongo.start();
        System.setProperty("MONGO_HOST", mongo.getHost());
        System.setProperty("MONGO_PORT", String.valueOf(mongo.getMappedPort(27017)));

        return mongo;
    }
}
