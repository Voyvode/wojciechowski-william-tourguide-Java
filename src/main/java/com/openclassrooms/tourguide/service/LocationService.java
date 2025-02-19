package com.openclassrooms.tourguide.service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import gpsUtil.GpsUtil;
import gpsUtil.location.VisitedLocation;

import com.openclassrooms.tourguide.model.User;

@Service
@RequiredArgsConstructor
public class LocationService {

	private final GpsUtil gpsUtil;
	private final RewardService rewardsService;
	private final Executor threadPool;

	public VisitedLocation getUserLocation(User user) {
		return user.getVisitedLocations().isEmpty() ? trackUserLocationAsync(user).join() : user.getLastVisitedLocation();
	}


	/**
	 * Tracks a user location, updates visited locations and calculates rewards.
	 *
	 * @param user the user to be tracked.
	 * @return a CompletableFuture containing the newly visited location of the user.
	 */
	public CompletableFuture<VisitedLocation> trackUserLocationAsync(User user) {
		return CompletableFuture.supplyAsync(() -> gpsUtil.getUserLocation(user.getId()), threadPool)
				.thenCompose(visitedLocation -> {
					user.addToVisitedLocations(visitedLocation);
					return rewardsService.calculateRewardsAsync(user).thenApply(unused -> visitedLocation);
				});
	}

}
