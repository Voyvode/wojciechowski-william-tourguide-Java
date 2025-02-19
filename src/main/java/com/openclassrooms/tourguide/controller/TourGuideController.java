package com.openclassrooms.tourguide.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.openclassrooms.tourguide.dto.NearbyAttractionDTO;
import gpsUtil.location.VisitedLocation;

import com.openclassrooms.tourguide.service.TourGuideService;
import com.openclassrooms.tourguide.model.User;
import com.openclassrooms.tourguide.model.UserReward;

import tripPricer.Provider;

@RestController
public class TourGuideController {

	@Autowired
	TourGuideService tourGuideService;
	
    @RequestMapping("/")
    public String index() {
        return "Greetings from TourGuide!";
    }
    
    @RequestMapping("/getLocation") 
    public VisitedLocation getLocation(@RequestParam String userName) {
    	return tourGuideService.getUserLocation(getUser(userName));
    }

	/**
	 * Get the closest five tourist attractions to the user.
	 *
	 * @param userName the user to look for nearby attractions
	 * @return a JSON response containing attraction's name and location,
	 *         user's location and possible reward points
	 */
	@RequestMapping("/getNearbyAttractions")
	public List<NearbyAttractionDTO> getNearbyAttractions(String userName) {

		var lastVisitedLocation = this.getUser(userName).getLastVisitedLocation();

		// TODO: ugly, need refactor
		return tourGuideService.getNearByAttractionsWithDistanceAndReward(lastVisitedLocation).stream().map(triple ->
				new NearbyAttractionDTO(
						triple.getLeft().attractionName,
						triple.getLeft().latitude, triple.getLeft().longitude,
						lastVisitedLocation.location.latitude, lastVisitedLocation.location.longitude,
						triple.getMiddle(), // Distance
						triple.getRight())) // Reward
				.toList();
	}
    
    @RequestMapping("/getRewards") 
    public List<UserReward> getRewards(@RequestParam String userName) {
    	return tourGuideService.getUserRewards(getUser(userName));
    }
       
    @RequestMapping("/getTripDeals")
    public List<Provider> getTripDeals(@RequestParam String userName) {
    	return tourGuideService.getTripDeals(getUser(userName));
    }
    
    private User getUser(String userName) {
    	return tourGuideService.getUser(userName);
    }

}