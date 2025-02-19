package com.openclassrooms.tourguide;

import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import gpsUtil.location.Location;
import org.junit.jupiter.api.Test;

import gpsUtil.location.VisitedLocation;

import com.openclassrooms.tourguide.model.User;
import com.openclassrooms.tourguide.service.AttractionService;
import com.openclassrooms.tourguide.service.LocationService;
import com.openclassrooms.tourguide.service.RewardService;
import com.openclassrooms.tourguide.service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class TestRewardService {

	@Autowired
	private LocationService locationService;

	@Autowired
	private UserService userService;

	@Autowired
	private RewardService rewardService;

	@Autowired
	private AttractionService attractionService;

	@Test
	public void userGetRewards() {
		userService.setInternalUserNumber(0);
		rewardService.resetProximityBuffer();

		var user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		var attraction = attractionService.getAttractions().get(0);

		user.addToVisitedLocations(new VisitedLocation(user.getId(), attraction, new Date()));

		locationService.trackUserLocationAsync(user).join();
		var userRewards = user.getUserRewards().values();

		userService.tracker.stopTracking();

		int numberOfRewards = userRewards.size();
		assertEquals(1, numberOfRewards);
	}

	@Test
	public void isWithinAttractionProximity() {
		var attraction = attractionService.getAttractions().get(0);
		var rightThere = new Location(attraction.latitude, attraction.longitude);
		assertTrue(rewardService.isWithinAttractionProximity(attraction, rightThere));
	}

	@Test
	public void nearAllAttractions() {
		userService.setInternalUserNumber(1);
		rewardService.setProximityBuffer(Integer.MAX_VALUE);

		var user = userService.getAllUsers().get(0);
		user.addToVisitedLocations(new VisitedLocation(user.getId(), attractionService.getAttractions().get(0), new Date()));

		rewardService.calculateRewardsAsync(user).join();

		userService.tracker.stopTracking();

		int numberOfAttractions = attractionService.getAttractions().size();
		int numberOfRewards = user.getUserRewards().size();
		assertEquals(numberOfAttractions, numberOfRewards);
	}

}
