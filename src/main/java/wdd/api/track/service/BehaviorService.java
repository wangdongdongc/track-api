package wdd.api.track.service;

import org.apache.commons.lang3.tuple.Pair;

public interface BehaviorService {

    String getBehaviorIdByTypeId(long typeId, long subTypeId);

    Pair<Long, Long> getTypeIdByBehaviorId(String behaviorId);
}
