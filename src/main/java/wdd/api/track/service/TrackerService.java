package wdd.api.track.service;

import wdd.api.track.model.Tracker;

import java.math.BigInteger;

public interface TrackerService {

    Tracker findTrackerById(String trackerId);

    Tracker findOrCreateTrackerByUserId(BigInteger userId);
}
