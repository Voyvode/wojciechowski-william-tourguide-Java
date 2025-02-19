package com.openclassrooms.tourguide;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import tripPricer.Provider;

@SpringBootTest
public class TestTourGuide {


	@Test
	public void getUserLocation() {
		Executor executor = Executors.newFixedThreadPool(64);
		GpsUtil gpsUtil = new GpsUtil();
		RewardService rewardService = new RewardService(gpsUtil, new RewardCentral(), executor);
		InternalTestHelper.setInternalUserNumber(0);
		UserService userService = new UserService(gpsUtil, rewardService, executor);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		VisitedLocation visitedLocation = userService.trackUserLocationAsync(user).join();
		userService.tracker.stopTracking();
		assertTrue(visitedLocation.userId.equals(user.getUserId()));
	}

	@Test
	public void addUser() {
		Executor executor = Executors.newFixedThreadPool(64);
		GpsUtil gpsUtil = new GpsUtil();
		RewardService rewardService = new RewardService(gpsUtil, new RewardCentral(), executor);
		InternalTestHelper.setInternalUserNumber(0);
		UserService userService = new UserService(gpsUtil, rewardService, executor);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

		userService.addUser(user);
		userService.addUser(user2);

		User retrivedUser = userService.getUser(user.getUserName());
		User retrivedUser2 = userService.getUser(user2.getUserName());

		userService.tracker.stopTracking();

		assertEquals(user, retrivedUser);
		assertEquals(user2, retrivedUser2);
	}

	@Test
	public void getAllUsers() {
		Executor executor = Executors.newFixedThreadPool(64);
		GpsUtil gpsUtil = new GpsUtil();
		RewardService rewardService = new RewardService(gpsUtil, new RewardCentral(), Executors.newFixedThreadPool(64));
		InternalTestHelper.setInternalUserNumber(0);
		UserService userService = new UserService(gpsUtil, rewardService, executor);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

		userService.addUser(user);
		userService.addUser(user2);

		List<User> allUsers = userService.getAllUsers();

		userService.tracker.stopTracking();

		assertTrue(allUsers.contains(user));
		assertTrue(allUsers.contains(user2));
	}

	@Test
	public void trackUser() {
		Executor executor = Executors.newFixedThreadPool(64);
		GpsUtil gpsUtil = new GpsUtil();
		RewardService rewardService = new RewardService(gpsUtil, new RewardCentral(), Executors.newFixedThreadPool(64));
		InternalTestHelper.setInternalUserNumber(0);
		UserService userService = new UserService(gpsUtil, rewardService, executor);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		VisitedLocation visitedLocation = userService.trackUserLocationAsync(user).join();

		userService.tracker.stopTracking();

		assertEquals(user.getUserId(), visitedLocation.userId);
	}

	@Test
	public void getNearbyAttractions() {
		Executor executor = Executors.newFixedThreadPool(64);
		GpsUtil gpsUtil = new GpsUtil();
		RewardService rewardService = new RewardService(gpsUtil, new RewardCentral(), Executors.newFixedThreadPool(64));
		InternalTestHelper.setInternalUserNumber(0);
		UserService userService = new UserService(gpsUtil, rewardService, executor);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		VisitedLocation visitedLocation = userService.trackUserLocationAsync(user).join();

		List<Attraction> attractions = userService.getNearByAttractions(visitedLocation);

		userService.tracker.stopTracking();

		assertEquals(5, attractions.size());
	}

	public void getTripDeals() {
		Executor executor = Executors.newFixedThreadPool(64);
		GpsUtil gpsUtil = new GpsUtil();
		RewardService rewardService = new RewardService(gpsUtil, new RewardCentral(), Executors.newFixedThreadPool(64));
		InternalTestHelper.setInternalUserNumber(0);
		UserService userService = new UserService(gpsUtil, rewardService, executor);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

		List<Provider> providers = userService.getTripDeals(user);

		userService.tracker.stopTracking();

		assertEquals(10, providers.size());
	}

}
