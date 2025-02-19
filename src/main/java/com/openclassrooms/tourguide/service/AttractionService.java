package com.openclassrooms.tourguide.service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;

import com.openclassrooms.tourguide.dto.NearbyAttractionDTO;
import com.openclassrooms.tourguide.model.User;
import com.openclassrooms.tourguide.util.Distance;

@Service
public class AttractionService {

	private final RewardService rewardService;

	private final Map<String, Attraction> attractions;

	public AttractionService(RewardService rewardService, GpsUtil gpsUtil) {
		this.rewardService = rewardService;

		attractions = gpsUtil.getAttractions().stream().collect(Collectors.toMap(
						attraction -> attraction.attractionName,
						attraction -> attraction,
						(existing, replacement) -> existing,
						HashMap::new));
	}

	/**
	 * Retrieves the 5 closest attractions to the specified location, sorted by distance.
	 *
	 * @param user the user whose location is retrieved
	 * @return a list of the 5 nearest attractions
	 */
	public List<NearbyAttractionDTO> getNearbyAttractions(User user) {
		return attractions.values().stream()
				.map(attraction -> new NearbyAttractionDTO(
						attraction,
						Distance.between(attraction, user.getLastVisitedLocation().location),
						rewardService.getRewardPoints(attraction, user)))
				.sorted(Comparator.comparingDouble(NearbyAttractionDTO::distance))
				.limit(5)
				.toList();
	}

	public Attraction getAttraction(String attractionName) {
		return attractions.get(attractionName);
	}

	public List<Attraction> getAttractions() {
		return attractions.values().stream().toList();
	}

}
