package com.reactivespring.controller;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.service.MoviesInfoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

@RestController
@RequestMapping("/v1")
public class MoviesInfoController {

    private final MoviesInfoService moviesInfoService;

    public MoviesInfoController(MoviesInfoService moviesInfoService) {
        this.moviesInfoService = moviesInfoService;
    }

    @GetMapping("/movieinfos/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<MovieInfo>> getMovieInfoById(@PathVariable String id) {
        return moviesInfoService.getMovieInfoById(id)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()))
                .log();
    }

    @GetMapping("/movieinfos")
    @ResponseStatus(HttpStatus.OK)
    public Flux<MovieInfo> getAllMovieInfos(@RequestParam(value = "year", required = false) Integer year) {
        if (Objects.nonNull(year)) {
            return moviesInfoService.getMovieInfoByYear(year).log();
        }
        return moviesInfoService.getAllMovieInfo().log();
    }

    @PostMapping("/movieinfos")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<MovieInfo> addMovieInfo(@RequestBody @Valid MovieInfo movieInfo) {
        return moviesInfoService.addMovieInfo(movieInfo).log();
    }

    @PutMapping("/movieinfos/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<MovieInfo>> updateMovieInfo(@RequestBody MovieInfo updateMovieInfo, @PathVariable String id) {
        return moviesInfoService.updateMovieInfo(updateMovieInfo, id)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build() ))
                .log();
    }

    @DeleteMapping("/movieinfos/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteMovieInfo(@PathVariable String id) {
        return moviesInfoService.deleteMovieInfo(id).log();
    }
}
