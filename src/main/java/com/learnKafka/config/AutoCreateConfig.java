package com.learnKafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@Profile("local")//so this will only run in our local
public class AutoCreateConfig {

    @Bean//Create a new Topic locally to test.
    public NewTopic libraryEvents()
    {
       return TopicBuilder.name("library-events")
                .partitions(3)
                .replicas(3)
                .build();
    }
}
