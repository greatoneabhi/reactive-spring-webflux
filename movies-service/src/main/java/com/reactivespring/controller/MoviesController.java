package com.reactivespring.controller;

import com.reactivespring.domain.Movie;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/movies")
public class MoviesController {

    @GetMapping("/{id}")
    public Mono<Movie> getMovieById(String movieId) {
        return Mono.error(new RuntimeException("Not Implemented"));
    }
}
