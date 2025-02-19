package com.openclassrooms.tourguide.tracker;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import org.apache.commons.lang3.time.StopWatch;

import com.openclassrooms.tourguide.model.User;
import com.openclassrooms.tourguide.service.LocationService;
import com.openclassrooms.tourguide.service.UserService;

@Component
@Slf4j
public class Tracker extends Thread {

	private static final long trackingPollingInterval = TimeUnit.MINUTES.toSeconds(5);
	private final ExecutorService executorService = Executors.newSingleThreadExecutor();
	private final UserService userService;
	private final LocationService locationService;
	private boolean stop = false;

	public Tracker(@Lazy UserService userService, LocationService locationService) {
		this.userService = userService;
		this.locationService = locationService;

		executorService.submit(this);
	}

	/**
	 * Assures to shut down the Tracker thread
	 */
	public void stopTracking() {
		stop = true;
		executorService.shutdownNow();
	}

	@Override
	public void run() {
		var stopWatch = new StopWatch();
		while (true) {
			if (Thread.currentThread().isInterrupted() || stop) {
				log.debug("Tracker stopping");
				break;
			}

			List<User> users = userService.getAllUsers();
			log.debug("Begin Tracker. Tracking {} users.", users.size());
			stopWatch.start();
			users.forEach(u -> locationService.trackUserLocationAsync(u).join());
			stopWatch.stop();
			log.debug("Tracker Time Elapsed: {} seconds.", stopWatch.getDuration().toSeconds());
			stopWatch.reset();
			try {
				log.debug("Tracker sleeping");
				TimeUnit.SECONDS.sleep(trackingPollingInterval);
			} catch (InterruptedException e) {
				break;
			}
		}

	}
}
