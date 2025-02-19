package com.openclassrooms.tourguide;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.apache.commons.lang3.time.StopWatch;

import gpsUtil.location.VisitedLocation;

import com.openclassrooms.tourguide.model.User;
import com.openclassrooms.tourguide.service.AttractionService;
import com.openclassrooms.tourguide.service.LocationService;
import com.openclassrooms.tourguide.service.RewardService;
import com.openclassrooms.tourguide.service.UserService;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Tag("performance")
@Slf4j
public class TestPerformance {

	@Autowired
	private UserService userService;

	@Autowired
	private LocationService locationService;

	@Autowired
	private AttractionService attractionService;

	@Autowired
	private RewardService rewardService;

	@Disabled
	@Test
	public void highVolumeTrackLocation() {
		userService.setInternalUserNumber(100_000);	// test number of users up to 100,000
		var stopWatch = new StopWatch();			// test must finish < 15 min (900 s)

		List<User> allUsers = userService.getAllUsers();
		List<CompletableFuture<VisitedLocation>> allAsyncs = new ArrayList<>();

		stopWatch.start();

		allUsers.forEach(user -> {
			var async = locationService.trackUserLocationAsync(user);
			allAsyncs.add(async);
		});
		CompletableFuture.allOf(allAsyncs.toArray(CompletableFuture[]::new)).join(); // execute all asyncs

		stopWatch.stop();
		userService.tracker.stopTracking();

		long elapsedTime = stopWatch.getDuration().toSeconds();
		long timeLimit = TimeUnit.MINUTES.toSeconds(15);
		assertTrue(elapsedTime < timeLimit);

		log.info("highVolumeTrackLocation: Time Elapsed: {} seconds.", elapsedTime);
	}

	@Disabled
	@Test
	public void highVolumeGetRewards() {
		userService.setInternalUserNumber(100_000);	// test number of users up to 100,000
		var stopWatch = new StopWatch();			// test must finish < 20 min (1,200 s)

		stopWatch.start();

		var attraction = attractionService.getAttractions().get(0);
		List<User> allUsers = userService.getAllUsers();
		List<CompletableFuture<Void>> allAsyncs = new ArrayList<>();

		allUsers.forEach(user -> user.addToVisitedLocations(new VisitedLocation(user.getId(), attraction, new Date())));

		allUsers.forEach(user -> {
			var async = rewardService.calculateRewardsAsync(user);
			allAsyncs.add(async);
		});
		CompletableFuture.allOf(allAsyncs.toArray(CompletableFuture[]::new)).join(); // execute all asyncs

		allUsers.forEach(user -> assertFalse(user.getUserRewards().isEmpty()));

		stopWatch.stop();
		userService.tracker.stopTracking();

		long elapsedTime = stopWatch.getDuration().toSeconds();
		long timeLimit = TimeUnit.MINUTES.toSeconds(20);
		assertTrue(elapsedTime < timeLimit);

		log.info("highVolumeGetRewards: Time Elapsed: {} seconds.", stopWatch.getDuration().toSeconds());
	}

}
