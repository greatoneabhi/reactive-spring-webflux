package com.reactivespring.controller;

import com.reactivespring.client.MovieReviewRestClient;
import com.reactivespring.client.MoviesInfoRestClient;
import com.reactivespring.domain.Movie;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/movies")
public class MoviesController {

    private final MoviesInfoRestClient moviesInfoRestClient;
    private final MovieReviewRestClient movieReviewRestClient;

    public MoviesController(MoviesInfoRestClient moviesInfoRestClient, MovieReviewRestClient movieReviewRestClient) {
        this.moviesInfoRestClient = moviesInfoRestClient;
        this.movieReviewRestClient = movieReviewRestClient;
    }

    @GetMapping("/{id}")
    public Mono<Movie> getMovieById(@PathVariable("id") String movieId) {

        return moviesInfoRestClient.retrieveMovieInfo(movieId)
                .flatMap(movieInfo -> {
                    var movieReviewsMono = movieReviewRestClient.retrieveMovieReviews(movieId)
                            .collectList();
                    return movieReviewsMono.map(reviews -> new Movie(movieInfo, reviews));
                });
    }
}
