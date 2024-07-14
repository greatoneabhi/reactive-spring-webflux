package com.reactivespring.repository;

import com.reactivespring.domain.MovieInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@ActiveProfiles("test")
class MovieInfoRepositoryIntegTest {

    @Autowired
    MovieInfoRepository movieInfoRepository;

    @BeforeEach
    void setUp() {

        var movieInfos = List.of(
                new MovieInfo(null, "Black Panther", 2018,
                        List.of("Action", "Adventure", "Fantasy"), LocalDate.parse("2005-06-15")),
                new MovieInfo(null, "Captain Marvel", 2019,
                        List.of("Action", "Adventure", "Sci-Fi"), LocalDate.parse("2008-07-12")),
                new MovieInfo(null, "The Equalizer 2", 2018,
                        List.of("Action", "Adventure", "Sci-Fi"), LocalDate.parse("2018-06-25")),
                new MovieInfo("abc", "Get Out", 2017,
                        List.of("Action", "Horror", "Thriller"), LocalDate.parse("2017-05-11"))
        );

        movieInfoRepository.saveAll(movieInfos).blockLast();

    }

    @AfterEach
    void tearDown() {
        movieInfoRepository.deleteAll().block();
    }

    @Test
    void findMovieByYear() {

        var moviesInfoFlux = movieInfoRepository.findByYear(2018).log();

        StepVerifier.create(moviesInfoFlux)
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void findMovieByName() {
        var movieInfoMono = movieInfoRepository.findByName("Get Out").log();

        StepVerifier.create(movieInfoMono)
                .assertNext(movieInfo -> {
                    assertEquals("abc", movieInfo.getMovieInfoId());
                    assertEquals("Get Out", movieInfo.getName());
                    assertEquals(2017, movieInfo.getYear());
                })
                .verifyComplete();
    }

    @Test
    void findAll() {

        var moviesInfoFlux = movieInfoRepository.findAll().log();

        StepVerifier.create(moviesInfoFlux)
                .expectNextCount(4)
                .verifyComplete();
    }

    @Test
    void findById() {

        var movieInfoMono = movieInfoRepository.findById("abc").log();

        StepVerifier.create(movieInfoMono)
                .assertNext(movieInfo -> {
                    assertEquals("Get Out", movieInfo.getName());
                })
                .verifyComplete();
    }

    @Test
    void saveMovieInfo() {

        var movieInfo = new MovieInfo(null, "The Equalizer 3", 2018,
                List.of("Action", "Adventure", "Sci-Fi"), LocalDate.parse("2018-06-25"));

        var movieInfoMono = movieInfoRepository.save(movieInfo).log();

        StepVerifier.create(movieInfoMono)
                .assertNext(movieInfo1 -> {
                    assertNotNull(movieInfo1.getMovieInfoId());
                    assertEquals("The Equalizer 3", movieInfo1.getName());
                })
                .verifyComplete();
    }

    @Test
    void updateMovieInfo() {

        var movieInfo = movieInfoRepository.findById("abc").block();
        assert movieInfo != null;
        assertEquals(2017, movieInfo.getYear());

        movieInfo.setYear(2019);
        var movieInfoMono = movieInfoRepository.save(movieInfo).log();

        StepVerifier.create(movieInfoMono)
                .assertNext(movieInfo1 -> {
                    assertEquals(2019, movieInfo1.getYear());
                })
                .verifyComplete();
    }

    @Test
    void deleteMovieInfo() {

        var movieInfoMono = movieInfoRepository.deleteById("abc").log();

        StepVerifier.create(movieInfoMono)
                .verifyComplete();

        var moviesInfoFlux = movieInfoRepository.findAll().log();

        StepVerifier.create(moviesInfoFlux)
                .expectNextCount(3)
                .verifyComplete();
    }
}