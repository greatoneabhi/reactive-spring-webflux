package com.reactivespring.client;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.exception.MoviesInfoClientException;
import com.reactivespring.exception.MoviesInfoServerException;
import com.reactivespring.util.RetryUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class MoviesInfoRestClient {

    private final WebClient webClient;

    @Value("${restClient.moviesInfoUrl}")
    private String movieInfosUrl;

    public MoviesInfoRestClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<MovieInfo> retrieveMovieInfo(String movieId) {

        var url = movieInfosUrl.concat("/{id}");
        return webClient.get()
                .uri(url, movieId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                    log.error("Error page returned {} ", clientResponse.statusCode().value());
                    if (clientResponse.statusCode().equals(HttpStatus.NOT_FOUND)) {
                        return Mono
                                .error(new MoviesInfoClientException("Movie details not found for Id: " + movieId,
                                        clientResponse.statusCode().value()));
                    }
                    return clientResponse.bodyToMono(String.class)
                            .flatMap(errorResponse -> Mono.error(new MoviesInfoClientException(errorResponse,
                                    clientResponse.statusCode().value())));
                })
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> {
                    log.error("Server error page returned {} ", clientResponse.statusCode().value());
                    return clientResponse.bodyToMono(String.class)
                            .flatMap(errorResponse -> Mono
                                    .error(new MoviesInfoServerException("Unexpected error. " + errorResponse)));
                })
                .bodyToMono(MovieInfo.class)
                //.retry(3)
                .retryWhen(RetryUtil.retrySpec())
                .log();
    }

    public Flux<MovieInfo> retrieveMovieInfoStream() {
        var movieStreamUrl = movieInfosUrl.concat("/stream");
        return webClient.get()
                .uri(movieStreamUrl)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                    log.error("Error page returned {} ", clientResponse.statusCode().value());
                    return clientResponse.bodyToMono(String.class)
                            .flatMap(errorResponse -> Mono.error(new MoviesInfoClientException(errorResponse,
                                    clientResponse.statusCode().value())));
                })
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> {
                    log.error("Server error page returned {} ", clientResponse.statusCode().value());
                    return clientResponse.bodyToMono(String.class)
                            .flatMap(errorResponse -> Mono
                                    .error(new MoviesInfoServerException("Unexpected error. " + errorResponse)));
                })
                .bodyToFlux(MovieInfo.class)
                //.retry(3)
                .retryWhen(RetryUtil.retrySpec())
                .log();
    }
}
