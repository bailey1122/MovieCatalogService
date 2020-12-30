package com.pr.moviecatalogservice.resources;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.pr.moviecatalogservice.models.CatalogItem;
import com.pr.moviecatalogservice.models.Movie;
import com.pr.moviecatalogservice.models.Rating;
import com.pr.moviecatalogservice.models.UserRating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

// microservice communication having one microservice call other microservices

@RestController
@RequestMapping("/catalog")
public class MovieCatalogResource {

    @Autowired // consumer (give me something)
    private RestTemplate restTemplate;

//    @Autowired
//    private WebClient.Builder webClientBuilder;

    @RequestMapping("/{userId}")
//    // the method on the proxy is called first. In the case when the service is down, the
//    // fallback is called. Hystrix doesn't have an opportunity to intersect two methods that
//    // are called in the API class in the case when one method is calling another one in the same class.
//    // Hystrix can intercept a call if an external class's method is calling the method but not
      // a method which is calling the method inside the class. Therefore, we can solve the problem
      // by taking the method out into another class, bean, or whatever. And we could have the main API
      // method calling not another method of the same class but the method of another instance.
//    // Hystrix creates a proxy class which is wrapper containing the circuit
//    // breaker logic and return the response to a caller
//    // it detects that a service is down. Hystrix wraps a class in a proxy class
//    // and it returns a proxy class that Hystrix's created and wrapped around it
//    // if the circuit breaks, then don't call getCatalog. Call getFallbackCatalog instead
//    @HystrixCommand(fallbackMethod = "getFallbackCatalog") // the method which shouldn't cause the whole thing to go down
    public List<CatalogItem> getCatalog(@PathVariable("userId") String userId) {

        // create an instance of the class, populate the properties to it, and get a fully form object
//        Movie movie = restTemplate.getForObject("http://localhost:8092/movies/smth", Movie.class);

//        UserRating ratings = restTemplate.getForObject("http://localhost:8093/ratingsdata/users/" + userId, UserRating.class); // a hint
//        UserRating ratings = restTemplate.getForObject("http://movie-data-service/ratingsdata/users/" + userId, UserRating.class); // a hint. It
//        // detects a service name, calls Eureka, gets the actual port (host and port), and makes the subsequent call

        UserRating ratings = getUserRating(userId);

        // replace each rating with the catalog.
        return ratings.getRatings().stream().map(rating -> {
//            for every rated movie, take the movie's ID and call the movie information API with that ID
//            Movie movie = restTemplate.getForObject("http://localhost:8092/movies/" + rating.getMovieId(), Movie.class);
//            Movie movie = restTemplate.getForObject("http://movie-info-service/movies/" + rating.getMovieId(), Movie.class);

            // get an instance of Movie. Get back an asynchronous object
//            Movie movie = webClientBuilder.build()
//                    .get()
//                    .uri("http://localhost:8092/movies/" + rating.getMovieId())
//                    .retrieve() // fetch
//                    .bodyToMono(Movie.class) // convert whatever body gets back into an instance of Movie
//                    .block();
//            // get the movie information from the movie object and the rating information from the looped object. Make two
//            // API calls
//            return new CatalogItem(movie.getName(), "Desc", rating.getRating());

            return getCatalogItem(rating);
        })
            .collect(Collectors.toList());


//        // get all rated movie IDs and for each movie ID, call movie info service and get details
//        return Collections.singletonList(
//                new CatalogItem("Transformers", "Test", 4)
//        );
    }

    @HystrixCommand(fallbackMethod = "getFallbackCatalogItem")
    private CatalogItem getCatalogItem(Rating rating) {
        Movie movie = restTemplate.getForObject("http://movie-info-service/movies/" + rating.getMovieId(), Movie.class);
        return new CatalogItem(movie.getName(), "Desc", rating.getRating());
    }

    private CatalogItem getFallbackCatalogItem(Rating rating) {
        return new CatalogItem("Movie name not found", "", rating.getRating());
    }

    @HystrixCommand(fallbackMethod = "getFallbackUserRating")
    private UserRating getUserRating(@PathVariable("userId") String userId) {
        return restTemplate.getForObject("http://movie-data-service/ratingsdata/users/" + userId, UserRating.class);
    }

    private UserRating getFallbackUserRating(@PathVariable("userId") String userId) {
        UserRating userRating = new UserRating();
        userRating.setUserId(userId);
        userRating.setRatings(Arrays.asList(
                new Rating("0", 0)
        ));
        return userRating;
    }

    // reduce the possibility of an error when a fallback method executes. Simple hard-coded verison
    public List<CatalogItem> getFallbackCatalog(@PathVariable String userId) {
        return Arrays.asList(new CatalogItem("No movie", "", 0));
    }
}
