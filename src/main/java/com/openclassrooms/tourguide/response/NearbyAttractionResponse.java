package com.openclassrooms.tourguide.response;

public record NearbyAttractionResponse(String attractionName,
								double attractionLatitude, double attractionLongitude,
								double userLatitude, double userLongitude,
								double distance, int rewardPoints) { }