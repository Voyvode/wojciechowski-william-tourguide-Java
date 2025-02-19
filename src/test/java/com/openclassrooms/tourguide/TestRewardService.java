package com.openclassrooms.tourguide;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.springframework.boot.test.context.SpringBootTest;

import org.junit.jupiter.api.Test;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;
import com.openclassrooms.tourguide.helper.InternalTestHelper;
import com.openclassrooms.tourguide.service.RewardService;
import com.openclassrooms.tourguide.service.UserService;
import com.openclassrooms.tourguide.model.User;
import com.openclassrooms.tourguide.model.UserReward;

@SpringBootTest
public class TestRewardService {

	@Test
	public void userGetRewards() {
		Executor executor = Executors.newFixedThreadPool(64);
		GpsUtil gpsUtil = new GpsUtil();
		RewardService rewardService = new RewardService(gpsUtil, new RewardCentral(), executor);

		InternalTestHelper.setInternalUserNumber(0);
		UserService userService = new UserService(gpsUtil, rewardService, executor);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		Attraction attraction = gpsUtil.getAttractions().get(0);
		user.addToVisitedLocations(new VisitedLocation(user.getUserId(), attraction, new Date()));
		userService.trackUserLocationAsync(user).join();
		List<UserReward> userRewards = user.getUserRewards();
		userService.tracker.stopTracking();
		assertTrue(userRewards.size() == 1);
	}

	@Test
	public void isWithinAttractionProximity() {
		Executor executor = Executors.newFixedThreadPool(64);
		GpsUtil gpsUtil = new GpsUtil();
		RewardService rewardService = new RewardService(gpsUtil, new RewardCentral(), executor);
		Attraction attraction = gpsUtil.getAttractions().get(0);
		assertTrue(rewardService.isWithinAttractionProximity(attraction, attraction));
	}

	@Test
	public void nearAllAttractions() {
		Executor executor = Executors.newFixedThreadPool(64);
		GpsUtil gpsUtil = new GpsUtil();
		RewardService rewardService = new RewardService(gpsUtil, new RewardCentral(), executor);
		rewardService.setProximityBuffer(Integer.MAX_VALUE);

		InternalTestHelper.setInternalUserNumber(1);
		UserService userService = new UserService(gpsUtil, rewardService, executor);

		rewardService.calculateRewardsAsync(userService.getAllUsers().get(0)).join();
		List<UserReward> userRewards = userService.getUserRewards(userService.getAllUsers().get(0));
		userService.tracker.stopTracking();

		assertEquals(gpsUtil.getAttractions().size(), userRewards.size());
	}

}
