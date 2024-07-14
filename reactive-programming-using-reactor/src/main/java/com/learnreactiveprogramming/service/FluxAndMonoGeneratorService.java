package com.learnreactiveprogramming.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

public class FluxAndMonoGeneratorService {

    public Flux<String> namesFlux() {
       return Flux.fromIterable(List.of("alex", "ben", "chloe"))
               .log();
    }

    public Mono<String> nameMono() {
        return Mono.just("alex")
                .log();
    }

    public  Flux<String> namesFluxMap() {
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .map(String::toUpperCase)
                .log();
    }

    public  Flux<String> namesFluxImmutability() {
        var namesFlux = Flux.fromIterable(List.of("alex", "ben", "chloe"));
        namesFlux.map(String::toUpperCase);
        return namesFlux;
    }

    public Flux<String> namesFluxFilter(int length) {
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .filter(name -> name.length() > length)
                .map(String::toUpperCase)
                .log();
    }

    public Flux<String> namesFluxFlatMap(int length) {
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .map(String::toUpperCase)
                .filter(name -> name.length() > length)
                .flatMap(this::splitString)
                .log();
    }

    public Flux<String> namesFluxFlatMapAsync(int length) {
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .map(String::toUpperCase)
                .filter(name -> name.length() > length)
                .flatMap(this::splitStringWithDelay)
                .log();
    }

    public Flux<String> namesFluxConcatMap(int length) {
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .map(String::toUpperCase)
                .filter(name -> name.length() > length)
                .concatMap(this::splitStringWithDelay)
                .log();
    }

    public Flux<String> splitString(String name) {
        return Flux.fromArray(name.split(""));
    }

    public Flux<String> splitStringWithDelay(String name) {
        var charArray = name.split("");
        var delay = new Random().nextInt(1000);
        return Flux.fromArray(charArray)
                .delayElements(Duration.ofMillis(delay));
    }

    public Mono<String> namesMonoMapFilter(int stringLength) {
        return Mono.just("alex")
                .map(String::toUpperCase)
                .filter(name -> name.length() > stringLength)
                .log();
    }

    public Mono<List<String>> namesMonoFlatMap(int stringLength) {
        return Mono.just("alex")
                .map(String::toUpperCase)
                .filter(name -> name.length() > stringLength)
                .flatMap(this::splitStringMono)
                .log();
    }

    public Flux<String> namesMonoFlatMapMany(int stringLength) {
        return Mono.just("alex")
                .map(String::toUpperCase)
                .filter(name -> name.length() > stringLength)
                .flatMapMany(this::splitString)
                .log();
    }

    private Mono<List<String>> splitStringMono(String s) {
        var charArray = s.split("");
        var charList = List.of(charArray);
        return Mono.just(charList);
    }

    public Flux<String> namesFluxTransform(int stringLength) {
        Function<Flux<String>, Flux<String>> filterMap = name -> name
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength);

        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .transform(filterMap)
                .flatMap(this::splitString)
                .defaultIfEmpty("default")
                .log();
    }

    public Flux<String> namesFluxTransformSwitchIfEmpty(int stringLength) {
        Function<Flux<String>, Flux<String>> filterMap = name -> name
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .flatMap(this::splitString);

        var defaultFlux = Flux.just("default")
                .transform(filterMap);

        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .transform(filterMap)
                .switchIfEmpty(defaultFlux)
                .log();
    }

    public Flux<String> exploreConcat() {
        var abcFlux = Flux.just("a", "b", "c");
        var defFlux = Flux.just("d", "e", "f");
        return Flux.concat(abcFlux, defFlux)
                .log();
    }

    public Flux<String> exploreConcatWith() {
        var abcFlux = Flux.just("a", "b", "c");
        var defFlux = Flux.just("d", "e", "f");
        return abcFlux.concatWith(defFlux)
                .log();
    }

    public Flux<String> exploreConcatWithMono() {
        var aMono = Mono.just("a");
        var bMono = Mono.just("b");
        return aMono.concatWith(bMono)
                .log();
    }

    public Flux<String> exploreMerge() {
        var abcFlux = Flux.just("a", "b", "c")
                .delayElements(Duration.ofMillis(100));
        var defFlux = Flux.just("d", "e", "f")
                .delayElements(Duration.ofMillis(125));
        return Flux.merge(abcFlux, defFlux)
                .log();
    }

    public Flux<String> exploreMergeWith() {
        var abcFlux = Flux.just("a", "b", "c")
                .delayElements(Duration.ofMillis(100));
        var defFlux = Flux.just("d", "e", "f")
                .delayElements(Duration.ofMillis(125));
        return abcFlux.mergeWith(defFlux)
                .log();
    }

    public Flux<String> exploreMergeWithMono() {
        var aMono = Mono.just("a");
        var bMono = Mono.just("b");
        return aMono.mergeWith(bMono)
                .log();
    }

    public Flux<String> exploreMergeSequential() {
        var abcFlux = Flux.just("a", "b", "c")
                .delayElements(Duration.ofMillis(100));
        var defFlux = Flux.just("d", "e", "f")
                .delayElements(Duration.ofMillis(125));
        return Flux.mergeSequential(abcFlux, defFlux)
                .log();
    }

    public Flux<String> exploreZip() {
        var abcFlux = Flux.just("a", "b", "c");
        var defFlux = Flux.just("d", "e", "f");

        return Flux.zip(abcFlux, defFlux, (first, second) -> first + second)
                .log();
    }

    public Flux<String> exploreZip1() {
        var abcFlux = Flux.just("a", "b", "c");
        var defFlux = Flux.just("d", "e", "f");
        var _123Flux = Flux.just("1", "2", "3");
        var _456Flux = Flux.just("4", "5", "6");

        return Flux.zip(abcFlux, defFlux, _123Flux, _456Flux)
                .map(tuples -> tuples.getT1() + tuples.getT2() + tuples.getT3() + tuples.getT4())
                .log();
    }

    public Flux<String> exploreZipWith() {
        var abcFlux = Flux.just("a", "b", "c");
        var defFlux = Flux.just("d", "e", "f");

        return abcFlux.zipWith(defFlux, (first, second) -> first + second)
                .log();
    }

    public Mono<String> exploreZipWithMono() {
        var aMono = Mono.just("a");
        var bMono = Mono.just("b");

        return aMono.zipWith(bMono)
                .map(t2 -> t2.getT1() + t2.getT2())
                .log();
    }

    //<editor-fold desc="Utility Methods">

    public static void main(String[] args) {

        FluxAndMonoGeneratorService  fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();
        fluxAndMonoGeneratorService.namesFlux()
                .subscribe(name -> System.out.println("Flux Name : " + name));

        fluxAndMonoGeneratorService.nameMono()
                .subscribe(name -> System.out.println("Mono Name : " + name));
    }
}
