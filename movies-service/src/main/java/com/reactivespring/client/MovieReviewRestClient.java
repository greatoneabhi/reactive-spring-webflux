package com.reactivespring.client;

import com.reactivespring.domain.Review;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;

@Component
public class MovieReviewRestClient {

    private final WebClient webClient;

    @Value("${restClient.moviesReviewUrl}")
    private String moviesReviewUrl;

    public MovieReviewRestClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public Flux<Review> retrieveMovieReviews(String movieInfoId) {
        var url = UriComponentsBuilder.fromUriString(moviesReviewUrl)
                .queryParam("movieInfoId", movieInfoId)
                .buildAndExpand()
                .toUriString();

        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToFlux(Review.class)
                .log();
    }
}
