package com.reactivespring.routes;


import com.reactivespring.domain.Review;
import com.reactivespring.exceptionhandler.GlobalExceptionHandler;
import com.reactivespring.handler.ReviewHandler;
import com.reactivespring.repository.MovieReviewRepository;
import com.reactivespring.router.ReviewRouter;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@WebFluxTest
@ContextConfiguration(classes = {ReviewRouter.class, ReviewHandler.class, GlobalExceptionHandler.class})
@AutoConfigureWebTestClient
public class ReviewsUnitTest {

    @MockBean
    MovieReviewRepository movieReviewRepository;

    @Autowired
    WebTestClient webTestClient;

    @Test
    void addReview() {

        var review = new Review("1", 2L, "Awesome Movie", 8.0);

        Mockito.when(movieReviewRepository.save(Mockito.isA(Review.class))).thenReturn(Mono.just(review));

        webTestClient
                .post()
                .uri("/v1/reviews")
                .bodyValue(review)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.movieInfoId").isEqualTo(2)
                .jsonPath("$.comment").isEqualTo("Awesome Movie")
                .jsonPath("$.rating").isEqualTo(8.0);
    }

    @Test
    void addReviewValidation() {

        var review = new Review("1", null, "Awesome Movie", -1.0);

        Mockito.when(movieReviewRepository.save(Mockito.isA(Review.class))).thenReturn(Mono.just(review));

        webTestClient
                .post()
                .uri("/v1/reviews")
                .bodyValue(review)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody(String.class)
                .isEqualTo("movieInfoId must not be null, rating : please pass a non-negative value");
    }

    @Test
    void getAllReviews() {

        var reviews = List.of(
                new Review("1", 2L, "Awesome Movie", 8.0),
                new Review("2", 2L, "Awesome Movie", 8.0)
        );

        Mockito.when(movieReviewRepository.findAll()).thenReturn(Flux.fromIterable(reviews));

        webTestClient
                .get()
                .uri("/v1/reviews")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Review.class)
                .hasSize(2);
    }

    @Test
    void updateReview() {

        var review = new Review("1", 2L, "Awesome Movie", 8.0);

        Mockito.when(movieReviewRepository.findById(Mockito.anyString())).thenReturn(Mono.just(review));
        review.setRating(9.0);
        Mockito.when(movieReviewRepository.save(Mockito.isA(Review.class))).thenReturn(Mono.just(review));

        webTestClient
                .put()
                .uri("/v1/reviews/1")
                .bodyValue(review)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.movieInfoId").isEqualTo(2)
                .jsonPath("$.comment").isEqualTo("Awesome Movie")
                .jsonPath("$.rating").isEqualTo(9.0);
    }

    @Test
    void deleteReview() {

        var review = new Review("1", 2L, "Awesome Movie", 8.0);
        Mockito.when(movieReviewRepository.findById(Mockito.anyString())).thenReturn(Mono.just(review));
        Mockito.when(movieReviewRepository.deleteById(Mockito.anyString())).thenReturn(Mono.empty());

        webTestClient
                .delete()
                .uri("/v1/reviews/1")
                .exchange()
                .expectStatus()
                .isNoContent();
    }

    @Test
    void getReviewByMovieInfoId() {

        var reviews = List.of(
                new Review("1", 2L, "Awesome Movie", 8.0),
                new Review("2", 2L, "Awesome Movie", 8.0)
        );

        Mockito.when(movieReviewRepository.findByMovieInfoId(Mockito.anyLong())).thenReturn(Flux.fromIterable(reviews));

        webTestClient
                .get()
                .uri("/v1/reviews?movieInfoId=2")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Review.class)
                .hasSize(2);

    }
}
