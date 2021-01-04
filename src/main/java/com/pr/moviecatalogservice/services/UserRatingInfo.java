package com.pr.moviecatalogservice.services;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.pr.moviecatalogservice.models.Rating;
import com.pr.moviecatalogservice.models.UserRating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Service
public class UserRatingInfo {

    @Autowired // consumer (give me something)
    private RestTemplate restTemplate;

    @HystrixCommand(fallbackMethod = "getFallbackUserRating",
            commandProperties = {
                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "2000"), // timeout
                    @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "5"), // the number of requests it needs to look at
                    @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "50"), // the percentage of requests allowed to fail
                    @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "5000") // how much the circuit breaker is gonna sleep
            })
    public UserRating getUserRating(@PathVariable("userId") String userId) {
        return restTemplate.getForObject("http://localhost:8093/ratingsdata/users/" + userId, UserRating.class);
    }

    public UserRating getFallbackUserRating(@PathVariable("userId") String userId) {
        UserRating userRating = new UserRating();
        userRating.setUserRating(Arrays.asList(
                new Rating("0", 0)
        ));
        return userRating;
    }
}
