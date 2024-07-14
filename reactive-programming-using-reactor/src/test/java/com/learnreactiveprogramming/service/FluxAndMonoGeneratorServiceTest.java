package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.util.List;

class FluxAndMonoGeneratorServiceTest {

    FluxAndMonoGeneratorService  fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();

    @Test
    void namesFlux() {

        // when
        var namesFlux = fluxAndMonoGeneratorService.namesFlux();

        // then
        StepVerifier.create(namesFlux)
                .expectNext("alex","ben","chloe")
                .verifyComplete();
    }

    @Test
    void namesFluxCount() {

        // when
        var namesFlux = fluxAndMonoGeneratorService.namesFlux();

        // then
        StepVerifier.create(namesFlux)
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void namesFluxMap() {

        // when
        var namesFlux = fluxAndMonoGeneratorService.namesFluxMap();

        // then
        StepVerifier.create(namesFlux)
                .expectNext("ALEX", "BEN", "CHLOE")
                .verifyComplete();
    }

    @Test
    void namesFluxImmutability() {

        // when
        var namesFlux = fluxAndMonoGeneratorService.namesFluxImmutability();

        // then
        StepVerifier.create(namesFlux)
                .expectNext("alex", "ben", "chloe")
                .verifyComplete();
    }

    @Test
    void namesFluxFilter() {
        // given
        int length = 4;

        // when
        var namesFlux = fluxAndMonoGeneratorService.namesFluxFilter(length);

        // then
        StepVerifier.create(namesFlux)
                .expectNext("CHLOE")
                .verifyComplete();
    }

    @Test
    void namesFluxFlatMap() {

        // given
        int length = 3;

        // when
        var namesFlux = fluxAndMonoGeneratorService.namesFluxFlatMap(length);

        // then
        StepVerifier.create(namesFlux)
                .expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
                .verifyComplete();
    }

    @Test
    void namesFluxFlatMapAsync() {

        // given
        int length = 3;

        // when
        var namesFlux = fluxAndMonoGeneratorService.namesFluxFlatMapAsync(length);

        // then
        StepVerifier.create(namesFlux)
                //.expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
                .expectNextCount(9)
                .verifyComplete();
    }

    @Test
    void namesFluxConcatMap() {

        // given
        int length = 3;

        // when
        var namesFlux = fluxAndMonoGeneratorService.namesFluxConcatMap(length);

        // then
        StepVerifier.create(namesFlux)
                .expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
                .verifyComplete();
    }

    @Test
    void namesMonoFlatMap() {
        // given
        int stringLength = 3;

        // when
        var namesFlux = fluxAndMonoGeneratorService.namesMonoFlatMap(stringLength);

        // then
        StepVerifier.create(namesFlux)
                .expectNext(List.of("A", "L", "E", "X"))
                .verifyComplete();
    }

    @Test
    void namesMonoFlatMapMany() {
        // given
        int stringLength = 3;

        // when
        var namesFlux = fluxAndMonoGeneratorService.namesMonoFlatMapMany(stringLength);

        // then
        StepVerifier.create(namesFlux)
                .expectNext("A", "L", "E", "X")
                .verifyComplete();
    }

    @Test
    void namesFluxTransform() {
        // given
        int length = 3;

        // when
        var namesFlux = fluxAndMonoGeneratorService.namesFluxTransform(length);

        // then
        StepVerifier.create(namesFlux)
                .expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
                .verifyComplete();
    }

    @Test
    void namesFluxTransformDefaultEmpty() {
        // given
        int length = 6;

        // when
        var namesFlux = fluxAndMonoGeneratorService.namesFluxTransform(length);

        // then
        StepVerifier.create(namesFlux)
                .expectNext("default")
                .verifyComplete();
    }

    @Test
    void namesFluxTransformSwitchIfEmpty() {
        // given
        int length = 6;

        // when
        var namesFlux = fluxAndMonoGeneratorService.namesFluxTransformSwitchIfEmpty(length);

        // then
        StepVerifier.create(namesFlux)
                .expectNext("D", "E", "F", "A", "U", "L", "T")
                .verifyComplete();
    }

    @Test
    void exploreConcat() {

        // when
        var concatFlux = fluxAndMonoGeneratorService.exploreConcat();

        // then
        StepVerifier.create(concatFlux)
                .expectNext("a", "b", "c", "d", "e", "f")
                .verifyComplete();
    }

    @Test
    void exploreConcatWith() {
        // when
        var concatFlux = fluxAndMonoGeneratorService.exploreConcatWith();

        // then
        StepVerifier.create(concatFlux)
                .expectNext("a", "b", "c", "d", "e", "f")
                .verifyComplete();
    }

    @Test
    void exploreConcatWithMono() {
        // when
        var concatFlux = fluxAndMonoGeneratorService.exploreConcatWithMono();

        // then
        StepVerifier.create(concatFlux)
                .expectNext("a", "b")
                .verifyComplete();
    }

    @Test
    void exploreMerge() {

        // when
        var mergeFlux = fluxAndMonoGeneratorService.exploreMerge();

        // then
        StepVerifier.create(mergeFlux)
                .expectNext("a", "d", "b", "e", "c", "f")
                .verifyComplete();
    }

    @Test
    void exploreMergeWith() {
        // when
        var mergeFlux = fluxAndMonoGeneratorService.exploreMergeWith();

        // then
        StepVerifier.create(mergeFlux)
                .expectNext("a", "d", "b", "e", "c", "f")
                .verifyComplete();
    }

    @Test
    void exploreMergeWithMono() {

        // when
        var mergeFlux = fluxAndMonoGeneratorService.exploreMergeWithMono();

        // then
        StepVerifier.create(mergeFlux)
                .expectNext("a", "b")
                .verifyComplete();
    }

    @Test
    void exploreMergeSequential() {

        // when
        var mergeFlux = fluxAndMonoGeneratorService.exploreMergeSequential();

        // then
        StepVerifier.create(mergeFlux)
                .expectNext("a", "b", "c", "d", "e", "f")
                .verifyComplete();
    }

    @Test
    void exploreZip() {

        // when
        var zipFlux = fluxAndMonoGeneratorService.exploreZip();

        // then
        StepVerifier.create(zipFlux)
                .expectNext("ad", "be", "cf")
                .verifyComplete();
    }

    @Test
    void exploreZip1() {

        // when
        var zipFlux = fluxAndMonoGeneratorService.exploreZip1();

        // then
        StepVerifier.create(zipFlux)
                .expectNext("ad14", "be25", "cf36")
                .verifyComplete();
    }

    @Test
    void exploreZipWith() {

        // when
        var zipFlux = fluxAndMonoGeneratorService.exploreZipWith();

        // then
        StepVerifier.create(zipFlux)
                .expectNext("ad", "be", "cf")
                .verifyComplete();
    }

    @Test
    void exploreZipWithMono() {

        // when
        var zipFlux = fluxAndMonoGeneratorService.exploreZipWithMono();

        // then
        StepVerifier.create(zipFlux)
                .expectNext("ab")
                .verifyComplete();
    }
}