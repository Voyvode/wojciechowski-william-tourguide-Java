package com.openclassrooms.tourguide.service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.stream.IntStream;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;

import com.openclassrooms.tourguide.tracker.Tracker;
import com.openclassrooms.tourguide.model.User;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

	public final Tracker tracker;

	public User getUser(String username) {
		return internalUserMap.get(username);
	}

	public List<User> getAllUsers() {
		return internalUserMap.values().stream().toList();
	}

	public void addUser(User user) {
		internalUserMap.putIfAbsent(user.getUsername(), user);
	}

	/* **************************************** */
	/*  Methods below are for internal testing  */
	/* **************************************** */
	// Database connection will be used for external users,
	// but internal users are provided and stored in memory
	// for testing purposes.
	@Getter
	public boolean testMode = false;
	@Getter
	private int internalUserNumber = 0; // up to 100,000 users for testing

	private final Map<String, User> internalUserMap = new HashMap<>();

	public void setInternalUserNumber(int internalUserNumber) {
		testMode = true;
		this.internalUserNumber = internalUserNumber;

		log.info("TestMode enabled");
		log.debug("Initializing users");
		initializeInternalUsers();
		log.debug("Finished initializing users");

		Runtime.getRuntime().addShutdownHook(new Thread(tracker::stopTracking));
	}

	private void initializeInternalUsers() {
		internalUserMap.clear();

		IntStream.range(0, internalUserNumber).forEach(i -> {
			var username = "internalUser" + i;
			var phone = "000";
			var email = username + "@tourGuide.com";
			var user = new User(UUID.randomUUID(), username, phone, email);
			generateUserLocationHistory(user);

			internalUserMap.put(username, user);
		});

		log.debug("Created {} internal test users.", internalUserNumber);
	}

	private void generateUserLocationHistory(User user) {
		IntStream.range(0, 3).forEach(i -> {
			user.addToVisitedLocations(new VisitedLocation(user.getId(),
					new Location(generateRandomLatitude(), generateRandomLongitude()), getRandomTime()));
		});
	}

	private double generateRandomLongitude() {
		double leftLimit = -180;
		double rightLimit = 180;
		return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
	}

	private double generateRandomLatitude() {
		double leftLimit = -85.05112878;
		double rightLimit = 85.05112878;
		return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
	}

	private Date getRandomTime() {
		var localDateTime = LocalDateTime.now().minusDays(new Random().nextInt(30));
		return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
	}

}

