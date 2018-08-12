package wdd.api.track.config;

import org.redisson.Redisson;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import wdd.api.track.util.RedisOperation;

@Configuration
public class RedisClientConfig {

    @Value("${redis.host}")
    private String redisHost;

    @Value("${redis.port}")
    private int redisPort;

    @Value("${redis.db}")
    private int redisDB;

    @Bean
    RedisOperation redisOperation() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://" + redisHost + ":" + redisPort)
                .setDatabase(redisDB);
        return new RedisOperation(Redisson.create(config));
    }
}
