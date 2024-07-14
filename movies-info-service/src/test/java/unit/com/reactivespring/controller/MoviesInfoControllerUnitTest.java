package com.reactivespring.controller;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.service.MoviesInfoService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@WebFluxTest(controllers = MoviesInfoController.class)
@AutoConfigureWebTestClient
public class MoviesInfoControllerUnitTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private MoviesInfoService moviesInfoService;

    static String MOVIE_INFO_BASE_PATH = "/v1/movieinfos";

    @Test
    void getAllMovies() {

        var movieInfos = List.of(
                new MovieInfo(null, "Black Panther", 2018,
                        List.of("Action", "Adventure", "Fantasy"), LocalDate.parse("2005-06-15")),
                new MovieInfo(null, "Captain Marvel", 2019,
                        List.of("Action", "Adventure", "Sci-Fi"), LocalDate.parse("2008-07-12")),
                new MovieInfo("abc", "Get Out", 2017,
                        List.of("Action", "Horror", "Thriller"), LocalDate.parse("2017-05-11"))
        );

        Mockito.when(moviesInfoService.getAllMovieInfo())
                .thenReturn(Flux.fromIterable(movieInfos));

        webTestClient.get()
                .uri(MOVIE_INFO_BASE_PATH)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var movieInfosRes = movieInfoEntityExchangeResult.getResponseBody();
                    assert movieInfosRes != null;
                    assertEquals(3, movieInfosRes.size());
                });
    }

    @Test
    void getMovieInfoById() {
        var movieInfo = new MovieInfo("abc", "Get Out", 2017,
                List.of("Action", "Horror", "Thriller"), LocalDate.parse("2017-05-11"));

        Mockito.when(moviesInfoService.getMovieInfoById(Mockito.anyString()))
                .thenReturn(Mono.just(movieInfo));

        webTestClient.get()
                .uri(MOVIE_INFO_BASE_PATH + "/abc")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var movieInfoRes = movieInfoEntityExchangeResult.getResponseBody();
                    assert movieInfoRes != null;
                    assertEquals("abc", movieInfoRes.getMovieInfoId());
                    assertEquals("Get Out", movieInfoRes.getName());
                });
    }

    @Test
    void addMovieInfo() {
        var movieInfo = new MovieInfo(null, "Black Panther", 2018,
                List.of("Action", "Adventure", "Fantasy"), LocalDate.parse("2005-06-15"));

        Mockito.when(moviesInfoService.addMovieInfo(Mockito.any()))
                .thenReturn(Mono.just(movieInfo));

        webTestClient.post()
                .uri(MOVIE_INFO_BASE_PATH)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var movieInfoRes = movieInfoEntityExchangeResult.getResponseBody();
                    assert movieInfoRes != null;
                    assertEquals("Black Panther", movieInfoRes.getName());
                    assertEquals(2018, movieInfoRes.getYear());
                });
    }

    @Test
    void updateMovieInfo() {
        var movieInfo = new MovieInfo("abc", "Get Out", 2017,
                List.of("Action", "Horror", "Thriller"), LocalDate.parse("2017-05-11"));

        Mockito.when(moviesInfoService.updateMovieInfo(Mockito.any(), Mockito.anyString()))
                .thenReturn(Mono.just(movieInfo)) ;

        webTestClient.put()
                .uri(MOVIE_INFO_BASE_PATH + "/abc")
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var movieInfoRes = movieInfoEntityExchangeResult.getResponseBody();
                    assert movieInfoRes != null;
                    assertEquals("abc", movieInfoRes.getMovieInfoId());
                    assertEquals("Get Out", movieInfoRes.getName());
                });
    }

    @Test
    void deleteMovieInfo() {
        Mockito.when(moviesInfoService.deleteMovieInfo(Mockito.anyString()))
                .thenReturn(Mono.empty());

        webTestClient.delete()
                .uri(MOVIE_INFO_BASE_PATH + "/abc")
                .exchange()
                .expectStatus()
                .isNoContent();
    }

    @Test
    void addMovieInfoBadRequest() {
        var movieInfo = new MovieInfo(null, null, -2018,
                List.of(""), LocalDate.parse("2005-06-15"));

        webTestClient.post()
                .uri(MOVIE_INFO_BASE_PATH)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody(String.class)
                .consumeWith(stringEntityExchangeResult -> {
                    var errorMessage = stringEntityExchangeResult.getResponseBody();
                    System.out.println(errorMessage);
                    assert errorMessage != null;
                    assertEquals("Genre must be present, movieInfo must have a name, value must be positive", errorMessage);
                });
    }
}
