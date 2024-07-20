package com.reactivespring.controller;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

public class SinksTest {

    @Test
    void sink() {

        Sinks.Many<Integer> replaySink = Sinks.many().replay().all();

        replaySink.emitNext(1, Sinks.EmitFailureHandler.FAIL_FAST);
        replaySink.emitNext(2, Sinks.EmitFailureHandler.FAIL_FAST);

        Flux<Integer> integerFlux = replaySink.asFlux();
        integerFlux.subscribe((i) -> {
            System.out.println("Subscriber 1 : " + i);
        });
        Flux<Integer> integerFlux1 = replaySink.asFlux();
        integerFlux1.subscribe((i) -> {
            System.out.println("Subscriber 2 : " + i);
        });

        replaySink.tryEmitNext(3);
    }

    @Test
    void sinkMulticast() {

        Sinks.Many<Integer> multicastSink = Sinks.many().multicast().onBackpressureBuffer();

        multicastSink.emitNext(1, Sinks.EmitFailureHandler.FAIL_FAST);
        multicastSink.emitNext(2, Sinks.EmitFailureHandler.FAIL_FAST);

        Flux<Integer> integerFlux = multicastSink.asFlux();
        integerFlux.subscribe((i) -> {
            System.out.println("Subscriber 1 : " + i);
        });

        Flux<Integer> integerFlux1 = multicastSink.asFlux();
        integerFlux1.subscribe((i) -> {
            System.out.println("Subscriber 2 : " + i);
        });
        multicastSink.emitNext(3, Sinks.EmitFailureHandler.FAIL_FAST);
    }

    @Test
    void sinkUnicast() {
        Sinks.Many<Integer> unicastsink = Sinks.many().unicast().onBackpressureBuffer();

        unicastsink.emitNext(1, Sinks.EmitFailureHandler.FAIL_FAST);
        unicastsink.emitNext(2, Sinks.EmitFailureHandler.FAIL_FAST);

        Flux<Integer> integerFlux = unicastsink.asFlux();
        integerFlux.subscribe((i) -> {
            System.out.println("Subscriber 1 : " + i);
        });

        Flux<Integer> integerFlux1 = unicastsink.asFlux();
        integerFlux1.subscribe((i) -> {
            System.out.println("Subscriber 2 : " + i);
        });
        unicastsink.emitNext(3, Sinks.EmitFailureHandler.FAIL_FAST);
    }
}
