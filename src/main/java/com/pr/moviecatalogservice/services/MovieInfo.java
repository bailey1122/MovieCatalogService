package com.pr.moviecatalogservice.services;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.pr.moviecatalogservice.models.CatalogItem;
import com.pr.moviecatalogservice.models.Movie;
import com.pr.moviecatalogservice.models.Rating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
// separate class which makes a call as a fallback
public class MovieInfo {

    @Autowired // consumer (give me something)
    private RestTemplate restTemplate;

    @HystrixCommand(fallbackMethod = "getFallbackCatalogItem",
        threadPoolKey = "movieInfoPool", // separate bulkhead
        threadPoolProperties = {
                @HystrixProperty(name = "coreSize", value = "20"), // thread pool size
                @HystrixProperty(name = "maxQueueSize", value = "10") // the number of waiting requests allowed in a queue before
                // they can get accessed to the thread
        })
    public CatalogItem getCatalogItem(Rating rating) {
        Movie movie = restTemplate.getForObject("http://localhost:8092/movies/" + rating.getMovieId(), Movie.class);
        return new CatalogItem(movie.getName(), "Desc", rating.getRating());
    }

    public CatalogItem getFallbackCatalogItem(Rating rating) {
        return new CatalogItem("Movie name not found", "", rating.getRating());
    }
}
