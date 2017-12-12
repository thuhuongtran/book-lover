package com.vimensa.chat.config;

import com.hazelcast.config.Config;
import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.config.MapConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HazelcastConfig {
    @Bean
    public Config config(){
        return new Config().addMapConfig(
                new MapConfig()
                .setName("users")
                .setEvictionPolicy(EvictionPolicy.LRU)
        ).addMapConfig(
                new MapConfig()
                .setName("roomHistory")
                .setEvictionPolicy(EvictionPolicy.LRU)
        ).addMapConfig(
                new MapConfig()
                .setName("emoji")
                .setEvictionPolicy(EvictionPolicy.LRU)
        );
    }
}
