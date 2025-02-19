package com.openclassrooms.tourguide.dto;

import gpsUtil.location.Attraction;

public record NearbyAttractionDTO(Attraction attraction, double distance, int reward) {}