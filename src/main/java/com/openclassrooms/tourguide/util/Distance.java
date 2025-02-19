package com.openclassrooms.tourguide.util;

import gpsUtil.location.Location;

import static java.lang.Math.acos;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;

public class Distance {

	private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;

	public static double between(Location loc1, Location loc2) {
		double lat1 = toRadians(loc1.latitude);
		double lon1 = toRadians(loc1.longitude);
		double lat2 = toRadians(loc2.latitude);
		double lon2 = toRadians(loc2.longitude);

		double angle = acos(sin(lat1) * sin(lat2) + cos(lat1) * cos(lat2) * cos(lon1 - lon2));

		double nauticalMiles = 60 * toDegrees(angle);
		return STATUTE_MILES_PER_NAUTICAL_MILE * nauticalMiles;
	}

}
