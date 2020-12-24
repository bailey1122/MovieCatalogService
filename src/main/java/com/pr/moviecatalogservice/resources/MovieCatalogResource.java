package com.pr.moviecatalogservice.resources;

import com.pr.moviecatalogservice.models.CatalogItem;
import com.pr.moviecatalogservice.models.Movie;
import com.pr.moviecatalogservice.models.Rating;
import com.pr.moviecatalogservice.models.UserRating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

// microservice communication having one microservice call other microservices

@RestController
@RequestMapping("/catalog")
public class MovieCatalogResource {

    @Autowired // consumer (give me something)
    private RestTemplate restTemplate;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @RequestMapping("/{userId}")
    public List<CatalogItem> getCatalog(@PathVariable("userId") String userId) {

        // create an instance of the class, populate the properties to it, and get a fully form object
//        Movie movie = restTemplate.getForObject("http://localhost:8092/movies/smth", Movie.class);

//        UserRating ratings = restTemplate.getForObject("http://localhost:8093/ratingsdata/users/" + userId, UserRating.class); // a hint
        UserRating ratings = restTemplate.getForObject("http://movie-data-service/ratingsdata/users/" + userId, UserRating.class); // a hint. It
        // detects a service name, calls Eureka, gets the actual port (host and port), and makes the subsequent call

        // replace each rating with the catalog.
        return ratings.getUserRating().stream().map(rating -> {
//            for every rated movie, take the movie's ID and call the movie information API with that ID
//            Movie movie = restTemplate.getForObject("http://localhost:8092/movies/" + rating.getMovieId(), Movie.class);
            Movie movie = restTemplate.getForObject("http://movie-info-service/movies/" + rating.getMovieId(), Movie.class);

            // get an instance of Movie. Get back an asynchronous object
//            Movie movie = webClientBuilder.build()
//                    .get()
//                    .uri("http://localhost:8092/movies/" + rating.getMovieId())
//                    .retrieve() // fetch
//                    .bodyToMono(Movie.class) // convert whatever body gets back into an instance of Movie
//                    .block();
            // get the movie information from the movie object and the rating information from the looped object. Make two
            // API calls
            return new CatalogItem(movie.getName(), "Desc", rating.getRating());
        })
            .collect(Collectors.toList());


//        // get all rated movie IDs and for each movie ID, call movie info service and get details
//        return Collections.singletonList(
//                new CatalogItem("Transformers", "Test", 4)
//        );
    }
}
