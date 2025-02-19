package com.openclassrooms.tourguide.model;

import lombok.Data;

import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;

@Data
public class UserReward {

	public final VisitedLocation visitedLocation;
	public final Attraction attraction;
	private final int rewardPoints;
	
}
