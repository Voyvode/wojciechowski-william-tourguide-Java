package com.openclassrooms.tourguide;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.junit.jupiter.api.Test;

import tripPricer.Provider;

import com.openclassrooms.tourguide.dto.NearbyAttractionDTO;
import com.openclassrooms.tourguide.model.User;
import com.openclassrooms.tourguide.service.AttractionService;
import com.openclassrooms.tourguide.service.LocationService;
import com.openclassrooms.tourguide.service.TripDealService;
import com.openclassrooms.tourguide.service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class TestTourGuide {

	@Autowired
	private UserService userService;

	@Autowired
	private LocationService locationService;

	@Autowired
	private TripDealService tripDealService;

	@Autowired
	private AttractionService attractionService;

	@Test
	public void getUserLocation() {
		userService.setInternalUserNumber(0);

		var user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		var visitedLocation = locationService.trackUserLocationAsync(user).join();

		userService.tracker.stopTracking();

		assertEquals(visitedLocation.userId, user.getId());
	}

	@Test
	public void addUser() {
		userService.setInternalUserNumber(0);

		var user1 = new User(UUID.randomUUID(), "jon1", "000", "jon@tourGuide.com");
		var user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

		userService.addUser(user1);
		userService.addUser(user2);

		var retrievedUser1 = userService.getUser(user1.getUsername());
		var retrievedUser2 = userService.getUser(user2.getUsername());

		userService.tracker.stopTracking();

		assertEquals(user1, retrievedUser1);
		assertEquals(user2, retrievedUser2);
	}

	@Test
	public void getAllUsers() {
		userService.setInternalUserNumber(0);

		var user1 = new User(UUID.randomUUID(), "jon1", "000", "jon@tourGuide.com");
		var user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

		userService.addUser(user1);
		userService.addUser(user2);

		List<User> allUsers = userService.getAllUsers();

		userService.tracker.stopTracking();

		assertTrue(allUsers.contains(user1));
		assertTrue(allUsers.contains(user2));
	}

	@Test
	public void trackUser() {
		userService.setInternalUserNumber(0);

		var user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		var visitedLocation = locationService.trackUserLocationAsync(user).join();

		userService.tracker.stopTracking();

		assertEquals(user.getId(), visitedLocation.userId);
	}

	@Test
	public void getNearbyAttractions() {
		userService.setInternalUserNumber(0);

		var user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		locationService.trackUserLocationAsync(user).join();

		List<NearbyAttractionDTO> nearbyAttractions = attractionService.getNearbyAttractions(user);

		userService.tracker.stopTracking();

		int numberOfNearbyAttractions = nearbyAttractions.size();
		assertEquals(5, numberOfNearbyAttractions);
	}

	@Test
	public void getTripDeals() {
		userService.setInternalUserNumber(0);

		var user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

		List<Provider> providers = tripDealService.getTripDeals(user);

		userService.tracker.stopTracking();

		int numberOfProviders = providers.size();
		assertEquals(5, numberOfProviders);
	}

}
