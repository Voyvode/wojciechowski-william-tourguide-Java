package com.openclassrooms.tourguide.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import tripPricer.Provider;
import tripPricer.TripPricer;

import com.openclassrooms.tourguide.model.User;
import com.openclassrooms.tourguide.model.UserReward;

@Service
@RequiredArgsConstructor
public class TripDealService {

	private static final String TRIP_PRICER_API_KEY = "test-server-api-key";

	private final TripPricer tripPricer = new TripPricer();

	public List<Provider> getTripDeals(User user) {
		int cumulativeRewardPoints = user.getUserRewards().values().stream().mapToInt(UserReward::getRewardPoints).sum();
		List<Provider> providers = tripPricer.getPrice(TRIP_PRICER_API_KEY, user.getId(),
				user.getUserPreferences().getNumberOfAdults(), user.getUserPreferences().getNumberOfChildren(),
				user.getUserPreferences().getTripDuration(), cumulativeRewardPoints);
		user.setTripDeals(providers);
		return providers;
	}

}
