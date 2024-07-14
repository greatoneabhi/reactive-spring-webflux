package com.reactivespring.controller;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.repository.MovieInfoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
class MoviesInfoControllerIntegTest {

    @Autowired
    MovieInfoRepository movieInfoRepository;

    @Autowired
    WebTestClient webTestClient;

    static String MOVIE_INFO_BASE_PATH = "/v1/movieinfos";

    @BeforeEach
    void setUp() {

        var movieInfos = List.of(
                new MovieInfo(null, "Black Panther", 2018,
                        List.of("Action", "Adventure", "Fantasy"), LocalDate.parse("2005-06-15")),
                new MovieInfo(null, "Captain Marvel", 2019,
                        List.of("Action", "Adventure", "Sci-Fi"), LocalDate.parse("2008-07-12")),
                new MovieInfo("abc", "Get Out", 2017,
                        List.of("Action", "Horror", "Thriller"), LocalDate.parse("2017-05-11"))
        );

        movieInfoRepository.saveAll(movieInfos).blockLast();

    }

    @AfterEach
    void tearDown() {
        movieInfoRepository.deleteAll().then().block();
    }

    @Test
    void addMovieInfo() {

        webTestClient.post()
                .uri(MOVIE_INFO_BASE_PATH )
                .bodyValue(new MovieInfo(null, "The Equalizer 2", 2018,
                        List.of("Action", "Adventure", "Sci-Fi"), LocalDate.parse("2018-06-25")))
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var savedMovieInfo = movieInfoEntityExchangeResult.getResponseBody();
                    assert savedMovieInfo != null;
                    assert savedMovieInfo.getMovieInfoId() != null;
                    assertEquals("The Equalizer 2", savedMovieInfo.getName());
                });
    }

    @Test
    void getAllMoviesInfo() {

        webTestClient.get()
                .uri(MOVIE_INFO_BASE_PATH)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var movieInfos = movieInfoEntityExchangeResult.getResponseBody();
                    assert movieInfos != null;
                    assertEquals(3, movieInfos.size());
                });
    }

    @Test
    void getMovieInfoById() {

        var movieinfoId = "abc";

        webTestClient.get()
                .uri(MOVIE_INFO_BASE_PATH + "/{id}", movieinfoId)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Get Out")
                .jsonPath("$.movieInfoId").isEqualTo(movieinfoId);
    }

    @Test
    void getMovieInfoByIdNotFound() {

        var movieinfoId = "def";

        webTestClient.get()
                .uri(MOVIE_INFO_BASE_PATH + "/{id}", movieinfoId)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void updateMovieInfo() {

        var movieinfoId = "abc";

        webTestClient.put()
                .uri(MOVIE_INFO_BASE_PATH + "/{id}", movieinfoId)
                .bodyValue(new MovieInfo(null, "Get Out", 2018,
                        List.of("Action", "Adventure", "Sci-Fi"), LocalDate.parse("2018-06-28")))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Get Out")
                .jsonPath("$.movieInfoId").isEqualTo(movieinfoId)
                .jsonPath("$.year").isEqualTo(2018);
    }

    @Test
    void deleteMovieInfoById() {
        var movieinfoId = "abc";

        webTestClient.delete()
                .uri(MOVIE_INFO_BASE_PATH + "/{id}", movieinfoId)
                .exchange()
                .expectStatus()
                .isNoContent();
    }

    @Test
    void updateMovieInfoNotFound() {

        var movieinfoId = "def";

        webTestClient.put()
                .uri(MOVIE_INFO_BASE_PATH + "/{id}", movieinfoId)
                .bodyValue(new MovieInfo(null, "Get Out", 2018,
                        List.of("Action", "Adventure", "Sci-Fi"), LocalDate.parse("2018-06-28")))
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void getMovieInfoByYear() {

        var year = 2018;

        var uri = UriComponentsBuilder.fromUriString(MOVIE_INFO_BASE_PATH)
                            .queryParam("year", year)
                            .buildAndExpand()
                            .toUri();

        webTestClient.get()
                .uri(uri)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(MovieInfo.class)
                .hasSize(1);
    }
}