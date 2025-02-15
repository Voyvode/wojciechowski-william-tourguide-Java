package com.openclassrooms.tourguide;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.springframework.boot.test.context.SpringBootTest;

import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;
import com.openclassrooms.tourguide.helper.InternalTestHelper;
import com.openclassrooms.tourguide.service.RewardsService;
import com.openclassrooms.tourguide.service.TourGuideService;
import com.openclassrooms.tourguide.user.User;

@SpringBootTest
@Tag("performance")
public class TestPerformance {

	@Disabled
	@Test
	public void highVolumeTrackLocation() {
		Executor executor = Executors.newFixedThreadPool(64);
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral(), executor);

		InternalTestHelper.setInternalUserNumber(100_000); // test number of users up to 100,000
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService, executor); // test must finish < 15 min (900 s)

		List<User> allUsers = tourGuideService.getAllUsers();
		List<CompletableFuture<VisitedLocation>> allAsyncs = new ArrayList<>();

		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		for (User user : allUsers) {
			var async = tourGuideService.trackUserLocationAsync(user);
			allAsyncs.add(async);
		}
		CompletableFuture.allOf(allAsyncs.toArray(CompletableFuture[]::new)).join();

		stopWatch.stop();
		tourGuideService.tracker.stopTracking();

		System.out.println("highVolumeTrackLocation: Time Elapsed: "
				+ TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");
		assertTrue(TimeUnit.MINUTES.toSeconds(15) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
	}

	@Disabled
	@Test
	public void highVolumeGetRewards() {
		Executor executor = Executors.newFixedThreadPool(64);
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral(), executor);

		InternalTestHelper.setInternalUserNumber(100_000); // test number of users up to 100,000
		StopWatch stopWatch = new StopWatch(); // test must finish < 20 min (1,200 s)
		stopWatch.start();
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService, executor);

		Attraction attraction = gpsUtil.getAttractions().get(0);
		List<User> allUsers = tourGuideService.getAllUsers();
		List<CompletableFuture<Void>> allAsyncs = new ArrayList<>(); // test must finish < 20 min (1,200 s)

		allUsers.forEach(u -> u.addToVisitedLocations(new VisitedLocation(u.getUserId(), attraction, new Date())));

		allUsers.forEach(u -> {
			var async = rewardsService.calculateRewardsAsync(u);
			allAsyncs.add(async);
		});
		CompletableFuture.allOf(allAsyncs.toArray(CompletableFuture[]::new)).join();

		for (User user : allUsers) {
			assertTrue(user.getUserRewards().size() > 0);
		}
		stopWatch.stop();
		tourGuideService.tracker.stopTracking();

		System.out.println("highVolumeGetRewards: Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime())
				+ " seconds.");
		assertTrue(TimeUnit.MINUTES.toSeconds(20) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
	}

}
