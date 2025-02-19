package com.openclassrooms.tourguide.controller;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import gpsUtil.location.VisitedLocation;
import tripPricer.Provider;

import com.openclassrooms.tourguide.dto.NearbyAttractionDTO;
import com.openclassrooms.tourguide.service.AttractionService;
import com.openclassrooms.tourguide.service.LocationService;
import com.openclassrooms.tourguide.service.RewardService;
import com.openclassrooms.tourguide.service.TripDealService;
import com.openclassrooms.tourguide.service.UserService;
import com.openclassrooms.tourguide.model.User;
import com.openclassrooms.tourguide.model.UserReward;

@RestController
@RequiredArgsConstructor
public class TourGuideController {

	private final LocationService locationService;
	private final AttractionService attractionService;
	private final RewardService rewardService;
	private final TripDealService tripDealService;
	private final UserService userService;

	@RequestMapping("/")
    public String index() {
        return "Greetings from TourGuide!";
    }
    
    @RequestMapping("/getLocation") 
    public VisitedLocation getLocation(@RequestParam String username) {
    	return locationService.getUserLocation(getUser(username));
    }

	/**
	 * Get the closest five tourist attractions to the user.
	 *
	 * @param username the user to look for nearby attractions
	 * @return a JSON response containing attraction's name and location, user's location
	 *         and possible reward points
	 */
	@RequestMapping("/getNearbyAttractions")
	public List<NearbyAttractionDTO> getNearbyAttractions(@RequestParam String username) {
		return attractionService.getNearbyAttractions(getUser(username));
	}
    
    @RequestMapping("/getRewards") 
    public List<UserReward> getRewards(@RequestParam String username) {
    	return rewardService.getUserRewards(getUser(username));
    }

    @RequestMapping("/getTripDeals")
    public List<Provider> getTripDeals(@RequestParam String username) {
    	return tripDealService.getTripDeals(getUser(username));
    }

	private User getUser(String username) {
		return userService.getUser(username);
	}

}