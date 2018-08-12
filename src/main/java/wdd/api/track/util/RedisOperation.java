package wdd.api.track.util;

import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;

import java.util.Map;
import java.util.concurrent.locks.Lock;

public class RedisOperation {

    private static final String COMMON_PREFIX = "[TRACK_API]";

    private RedissonClient redissonClient;

    public RedisOperation(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    public <K, V> RMapCache<K, V> getMapCache(String name) {
        return redissonClient.getMapCache(COMMON_PREFIX + name);
    }

    public Lock getLock(String name) {
        return redissonClient.getLock(COMMON_PREFIX + name);
    }
}
