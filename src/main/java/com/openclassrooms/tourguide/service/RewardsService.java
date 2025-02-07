package com.openclassrooms.tourguide.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;
import com.openclassrooms.tourguide.user.User;
import com.openclassrooms.tourguide.user.UserReward;

@Service
public class RewardsService {
    private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;

	// proximity in miles
    private int defaultProximityBuffer = 10;
	private int proximityBuffer = defaultProximityBuffer;
	private int attractionProximityRange = 200;
	private final RewardCentral rewardsCentral;
	private final List<Attraction> attractions;
	
	public RewardsService(GpsUtil gpsUtil, RewardCentral rewardCentral) {
		this.rewardsCentral = rewardCentral;
		this.attractions = List.copyOf(gpsUtil.getAttractions());
	}
	
	public void setProximityBuffer(int proximityBuffer) {
		this.proximityBuffer = proximityBuffer;
	}
	
	public void setDefaultProximityBuffer() {
		proximityBuffer = defaultProximityBuffer;
	}

	/**
	 * Calculates and adds rewards for a user based on visited attractions.
	 *
	 * @param user the user to calculate rewards for, containing visited locations and current rewards.
	 */
	public void calculateRewards(User user) {
		var visitedAttractions = user.getUserRewards().stream()
				.map(reward -> reward.attraction)
				.collect(Collectors.toSet());

		var notVisitedAttractions = attractions.stream()
				.filter(attraction -> !visitedAttractions.contains(attraction))
				.toList();

		notVisitedAttractions.parallelStream().forEach(attraction ->
				user.getVisitedLocations().stream()
						.filter(visitedLocation -> nearAttraction(visitedLocation, attraction))
						.findFirst().ifPresent(visitedLocation -> user.addUserReward(
								new UserReward(visitedLocation, attraction, getRewardPoints(attraction, user))
						)));
	}
	
	public boolean isWithinAttractionProximity(Attraction attraction, Location location) {
		return getDistance(attraction, location) > attractionProximityRange ? false : true;
	}
	
	private boolean nearAttraction(VisitedLocation visitedLocation, Attraction attraction) {
		return getDistance(attraction, visitedLocation.location) > proximityBuffer ? false : true;
	}
	
	private int getRewardPoints(Attraction attraction, User user) {
		return rewardsCentral.getAttractionRewardPoints(attraction.attractionId, user.getUserId());
	}

	public List<Attraction> getAttractions() {
		return attractions;
	}

	public Attraction getAttraction(String attractionName) {
		return attractions.stream().filter(attraction -> attraction.attractionName.equals(attractionName)).findFirst().orElse(null);
	}
	
	public double getDistance(Location loc1, Location loc2) {
        double lat1 = Math.toRadians(loc1.latitude);
        double lon1 = Math.toRadians(loc1.longitude);
        double lat2 = Math.toRadians(loc2.latitude);
        double lon2 = Math.toRadians(loc2.longitude);

        double angle = Math.acos(Math.sin(lat1) * Math.sin(lat2)
                               + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));

        double nauticalMiles = 60 * Math.toDegrees(angle);
        double statuteMiles = STATUTE_MILES_PER_NAUTICAL_MILE * nauticalMiles;
        return statuteMiles;
	}

}
