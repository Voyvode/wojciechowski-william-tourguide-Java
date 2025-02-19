package com.openclassrooms.tourguide.config;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.support.TaskExecutorAdapter;
import org.springframework.scheduling.annotation.EnableAsync;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import rewardCentral.RewardCentral;

@Configuration
@EnableAsync
public class AppConfig {

	@Value("${threadpool.size}")
	private int threadPoolSize;


	static {
		Locale.setDefault(Locale.US);
	}

	@Bean
	public GpsUtil getGpsUtil() {
		return new GpsUtil();
	}

	@Bean
	public List<Attraction> attractions(GpsUtil gpsUtil) {
		return List.copyOf(gpsUtil.getAttractions());
	}

	@Bean
	public RewardCentral getRewardCentral() {
		return new RewardCentral();
	}

 	@Bean(name = "fixedThreadPool")
	@Primary
	public Executor getExecutor() {
		var fixedThreadPool = Executors.newFixedThreadPool(threadPoolSize);
		return new TaskExecutorAdapter(fixedThreadPool);
	}

}
