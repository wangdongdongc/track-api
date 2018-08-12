package wdd.api.track.service.impl;

import org.redisson.api.RMapCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import wdd.api.track.model.Tracker;
import wdd.api.track.service.TrackerService;
import wdd.api.track.util.RedisMethodLock;
import wdd.api.track.util.RedisOperation;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 基于 redis 的 {@link TrackerService} 实现
 */
@Service
public class RedisTrackerServiceImpl implements TrackerService {

    private static final String TRACKER_MAP = "trackerMap";
    private static final String TRACKER_ID_MAP = "trackerIdMap";

    @Value("${tracker.expiredSeconds}")
    private int expiredSeconds;

    private RMapCache<String, Tracker> trackerMap;
    private RMapCache<BigInteger, String> trackerIdMap;

    @Autowired
    public RedisTrackerServiceImpl(RedisOperation redisOperation) {
        this.trackerMap = redisOperation.getMapCache(TRACKER_MAP);
        this.trackerIdMap = redisOperation.getMapCache(TRACKER_ID_MAP);
    }

    @Override
    public Tracker findTrackerById(String trackerId) {
        return trackerMap.get(trackerId);
    }

    @Override
    @RedisMethodLock(useArgs = true)
    public Tracker findOrCreateTrackerByUserId(BigInteger userId) {
        String trackerId = trackerIdMap.get(userId);
        if (trackerId != null) {
            Tracker tracker = trackerMap.get(trackerId);
            if (tracker != null) {
                return tracker;
            }
        }

        Tracker tracker = new Tracker();
        tracker.setTrackerId(UUID.randomUUID().toString().replace("-", ""));
        tracker.setUserId(userId);

        trackerIdMap.put(userId, tracker.getTrackerId(), expiredSeconds, TimeUnit.SECONDS);
        trackerMap.put(tracker.getTrackerId(), tracker, expiredSeconds, TimeUnit.SECONDS);
        return tracker;
    }
}
