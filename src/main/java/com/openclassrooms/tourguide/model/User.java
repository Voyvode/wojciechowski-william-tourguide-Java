package com.openclassrooms.tourguide.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedDeque;

import lombok.Data;

import gpsUtil.location.VisitedLocation;
import tripPricer.Provider;

@Data
public class User {
	private final UUID id;
	private final String username;

	private String phoneNumber;
	private String emailAddress;
	private UserPreferences userPreferences = new UserPreferences();
	private List<Provider> tripDeals = new ArrayList<>();
	private Instant latestLocationTimestamp;

	private final Deque<VisitedLocation> visitedLocations = new ConcurrentLinkedDeque<>();
	private final Map<String, UserReward> userRewards = new HashMap<>();

	public User(UUID id, String username, String phoneNumber, String emailAddress) {
		this.id = id;
		this.username = username;
		this.phoneNumber = phoneNumber;
		this.emailAddress = emailAddress;
	}

	public VisitedLocation getLastVisitedLocation() {
		return visitedLocations.getLast();
	}

	public void addToVisitedLocations(VisitedLocation visitedLocation) {
		visitedLocations.add(visitedLocation);
	}

	public void clearVisitedLocations() {
		visitedLocations.clear();
	}

	public void addUserReward(UserReward userReward) {
		String attractionName = userReward.attraction.attractionName;
		userRewards.putIfAbsent(attractionName, userReward);
	}

}
