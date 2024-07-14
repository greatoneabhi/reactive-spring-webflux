package com.reactivespring.routes;

import com.reactivespring.domain.Review;
import com.reactivespring.repository.MovieReviewRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
public class ReviewsIntgTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    MovieReviewRepository movieReviewRepository;

    static String MOVIE_REVIEW_URL = "/v1/reviews";

    @BeforeEach
    void setUp() {

        var movieReviews = List.of(
                new Review("1", 1L, "Awesome movie", 9.0),
                new Review("2", 1L, "Great movie", 8.0),
                new Review("3", 2L, "Excellent movie", 8.0)
        );

        movieReviewRepository.saveAll(movieReviews).collectList().block();
    }

    @AfterEach
    void tearDown() {
        movieReviewRepository.deleteAll().block();
    }

    @Test
    void createMovieReview() {

        var review = new Review(null, 2L, "Awesome movie 2", 9.5);

        webTestClient
                .post()
                .uri(MOVIE_REVIEW_URL)
                .bodyValue(review)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("$.movieInfoId").isEqualTo(2l)
                .jsonPath("$.comment").isEqualTo("Awesome movie 2")
                .jsonPath("$.rating").isEqualTo(9.5);
    }

    @Test
    void getAllReviews() {

        webTestClient
                .get()
                .uri(MOVIE_REVIEW_URL)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(Review.class)
                .hasSize(3);
    }

    @Test
    void updateMovieReview() {

        var review = new Review(null, 2L, "Awesome movie", 9.5);
        var reviewId = "3";

        webTestClient
                .put()
                .uri(MOVIE_REVIEW_URL + "/{id}", reviewId)
                .bodyValue(review)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("$.movieInfoId").isEqualTo(2L)
                .jsonPath("$.comment").isEqualTo("Awesome movie")
                .jsonPath("$.rating").isEqualTo(9.5);
    }

    @Test
    void deleteMovieReview() {

        var reviewId = "3";

        webTestClient
                .delete()
                .uri(MOVIE_REVIEW_URL + "/{id}", reviewId)
                .exchange()
                .expectStatus()
                .isNoContent();

    }

    @Test
    void getReviewByMovieInfoId() {

        var movieInfoId = "1";

        var uri = UriComponentsBuilder.fromUriString(MOVIE_REVIEW_URL)
                .queryParam("movieInfoId", movieInfoId)
                .build().toUri();

        webTestClient
                .get()
                .uri(uri)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(Review.class)
                .hasSize(2);
    }
}
