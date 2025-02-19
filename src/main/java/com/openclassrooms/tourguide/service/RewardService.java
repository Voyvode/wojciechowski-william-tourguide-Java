package com.openclassrooms.tourguide.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import lombok.RequiredArgsConstructor;
import lombok.Setter;

import org.springframework.stereotype.Service;

import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;

import com.openclassrooms.tourguide.util.Distance;
import com.openclassrooms.tourguide.model.User;
import com.openclassrooms.tourguide.model.UserReward;

@Service
@RequiredArgsConstructor
public class RewardService {

	private final static int ATTRACTION_PROXIMITY_RANGE = 200; // miles
	private final static int DEFAULT_PROXIMITY_BUFFER = 10; // miles
	@Setter
	private int proximityBuffer = DEFAULT_PROXIMITY_BUFFER;

	private final List<Attraction> attractions;
	private final RewardCentral rewardsCentral;
	private final Executor threadPool;

	/**
	 * Calculates and adds rewards for a user based on visited attractions.
	 *
	 * @param user the user to calculate rewards for, containing visited locations and current rewards.
	 * @return a CompletableFuture that completes when all reward calculations and updates are done
	 */
	public CompletableFuture<Void> calculateRewardsAsync(User user) {
		var addUserRewardFutures = 	user.getVisitedLocations().stream()
				.flatMap(visitedLocation -> attractions.stream()
						.filter(attraction -> !user.getUserRewards().containsKey(attraction.attractionName))
						.filter(attraction -> isNearAttraction(visitedLocation, attraction))
						.map(attraction ->
								CompletableFuture.runAsync(() ->
										user.addUserReward(new UserReward(visitedLocation,	attraction,	getRewardPoints(attraction, user))
								), threadPool)))
				.toArray(CompletableFuture[]::new);

		return CompletableFuture.allOf(addUserRewardFutures);
	}

	public void resetProximityBuffer() {
		proximityBuffer = DEFAULT_PROXIMITY_BUFFER;
	}

	public boolean isWithinAttractionProximity(Attraction attraction, Location location) {
		return Distance.between(attraction, location) < ATTRACTION_PROXIMITY_RANGE;
	}
	
	private boolean isNearAttraction(VisitedLocation visitedLocation, Attraction attraction) {
		return Distance.between(attraction, visitedLocation.location) < proximityBuffer;
	}

	public List<UserReward> getUserRewards(User user) {
		return user.getUserRewards().values().stream().toList();
	}

	public int getRewardPoints(Attraction attraction, User user) {
		return rewardsCentral.getAttractionRewardPoints(attraction.attractionId, user.getId());
	}

}
