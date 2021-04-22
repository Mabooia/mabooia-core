package com.mabooia.collections.streams;

import com.mabooia.Lazy;
import com.mabooia.collections.streams.impl.ConsStream;
import com.mabooia.collections.streams.impl.EmptyStream;
import com.mabooia.collections.streams.impl.LazyStream;
import com.mabooia.collections.streams.impl.LazyTailStream;
import com.mabooia.concurrent.Tasks;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class Streams {

    private static final Stream<?> EMPTY_STREAM = new EmptyStream();

    @SuppressWarnings("unchecked")
    public static <A> Stream<A> emptyStream() {
        return (Stream<A>) EMPTY_STREAM;
    }

    public static <A> Stream<A> singleton(final A head) {
        return new ConsStream<>(head, emptyStream());
    }

    public static <A> Stream<A> of(final A head, final Supplier<Stream<A>> getTail) {
        return new LazyTailStream<>(head, Lazy.of(getTail));
    }

    public static <A> Stream<A> of(final A head, final Stream<A> tail) {
        return new ConsStream<>(head, tail);
    }

    public static <A> Stream<A> of(final Supplier<Stream<A>> getStream) {
        return new LazyStream<>(Lazy.of(getStream));
    }

    public static Stream<Integer> intStartingAt(final int start) {
        return of(start, () -> intStartingAt(start + 1));
    }

    public static <A> Stream<A> getDelayedStream(final Stream<A> stream,
                                                 final int delayGetHeadInMillis,
                                                 final int delayedGetTailInMillis) {
        final Lazy<Optional<A>> lazyHead = Lazy.of(() -> {
            Tasks.sleep(delayGetHeadInMillis);
            return stream.getHeadIfPresent();
        });
        final Lazy<Stream<A>> lazyTail = Lazy.of(() -> {
            Tasks.sleep(delayedGetTailInMillis);
            return getDelayedStream(stream.getTail(), delayGetHeadInMillis, delayedGetTailInMillis);
        });

        return new Stream<>() {
            @Override
            public Optional<A> getHeadIfPresent() {
                return lazyHead.get();
            }

            @Override
            public Stream<A> getTail() {
                return lazyTail.get();
            }
        };
    }

    @SafeVarargs
    public static <A> Stream<A> asStream(final A...array) {
        Stream<A> res = emptyStream();

        if (array == null || array.length == 0) {
            return res;
        }

        for (int idx = array.length - 1; idx >= 0; idx--) {
            res = of(array[idx], res);
        }

        return res;
    }

    public static <A> Stream<A> asStream(final Iterable<A> iterable) {
        if (iterable instanceof Stream) {
            return (Stream<A>) iterable;
        }
        return asStream(iterable.iterator());
    }

    public static <A> Stream<A> repeat(final A item, final int times) {
        return times > 0
            ? of(item, () -> repeat(item, times - 1))
            : emptyStream();
    }

    public static <A> Stream<A> repeat(final A item) {
        return of(item, () -> repeat(item));
    }

    public static <A> Stream<A> flatten(final Stream<Optional<A>> stream) {
        return stream
            .flatMap2(Function.identity());
    }

    public static <A> Stream<A> flatten2(final Stream<Stream<A>> stream) {
        return stream
            .flatMap(Function.identity());
    }

    public static <A, IT extends Iterable<A>> Stream<A> flatten3(final Stream<IT> stream) {
        return stream
            .flatMap3(Function.identity());
    }

    public static <A, B> Map<A, B> toMap(final Stream<? extends Map.Entry<A, B>> stream) {
        final Map<A, B> map = new LinkedHashMap<>();
        stream
            .forEach(entry -> map.put(entry.getKey(), entry.getValue()));
        return map;
    }

    static <A> Stream<A> filterIf(final Stream<A> stream,
                                  final Predicate<? super A> p,
                                  final boolean isTrue) {
        return of(() -> {
            final Stream<A> skippedHeadStream = skipWhileIf(stream, p, !isTrue);

            return skippedHeadStream
                .getHeadIfPresent()
                .map(head -> of(head, () -> filterIf(skippedHeadStream.getTail(), p, isTrue)))
                .orElseGet(Streams::emptyStream);
        });
    }

    static <A, B> Stream<B> map(final Stream<A> stream,
                                final Function<? super A, B> f) {
        return of(() -> stream
            .getHeadIfPresent()
            .map(head -> of(f.apply(head), () -> map(stream.getTail(), f)))
            .orElseGet(Streams::emptyStream)
        );
    }

    static <A> Stream<A> skipWhileIf(final Stream<A> stream,
                                     final Predicate<? super A> p,
                                     final boolean isTrue) {
        return of(() -> {
            Stream<A> curr = stream;
            Optional<A> currHead;
            while ((currHead = curr.getHeadIfPresent()).isPresent()
                && p.test(currHead.get()) == isTrue) {
                curr = curr.getTail();
            }
            return curr;
        });
    }

    static <A> Stream<A> skipWhileWithIndexIf(final Stream<A> stream,
                                              final BiPredicate<? super A, Integer> p,
                                              final boolean isTrue) {
        return of(() -> {
            int idx = 0;
            Stream<A> curr = stream;
            Optional<A> currHead;
            while ((currHead = curr.getHeadIfPresent()).isPresent()
                && p.test(currHead.get(), idx) == isTrue) {
                curr = curr.getTail();
                idx++;
            }
            return curr;
        });
    }

    static <A> Stream<A> takeWhileIf(final Stream<A> stream,
                                     final Predicate<? super A> p,
                                     final boolean isTrue) {
        return of(() -> stream
            .getHeadIfPresent()
            .filter(head -> p.test(head) == isTrue)
            .map(head -> of(head, () -> takeWhileIf(stream.getTail(), p, isTrue)))
            .orElseGet(Streams::emptyStream)
        );
    }

    static <A> Stream<A> takeWhileWithIndexIf(final Stream<A> stream,
                                              final int idx,
                                              final BiPredicate<? super A, Integer> p,
                                              final boolean isTrue) {
        return of(() -> stream
            .getHeadIfPresent()
            .filter(head -> p.test(head, idx) == isTrue)
            .map(head -> of(head, () -> takeWhileWithIndexIf(stream.getTail(), idx + 1, p, isTrue)))
            .orElseGet(Streams::emptyStream)
        );
    }

    static <A, B> Stream<Pair<A, B>> zip(final Stream<A> aStream, final Stream<B> bStream) {
        return of(() -> {
            final Optional<A> aHead = aStream.getHeadIfPresent();
            if (aHead.isEmpty()) {
                return emptyStream();
            }

            final Optional<B> bHead = bStream.getHeadIfPresent();
            if (bHead.isEmpty()) {
                return emptyStream();
            }

            return of(
                Pair.of(aHead.get(), bHead.get()),
                () -> zip(aStream.getTail(), bStream.getTail())
            );
        });
    }

    private static <A> Stream<A> asStream(final Iterator<A> iterator) {
        return new Stream<>() {
            private final Lazy<Optional<A>> lazyHead = Lazy.of(() -> {
                if (iterator.hasNext()) {
                    return Optional.of(iterator.next());
                }

                return Optional.empty();
            });

            @Override
            public Optional<A> getHeadIfPresent() {
                return lazyHead.get();
            }

            @Override
            public Stream<A> getTail() {
                if (nonEmpty()) {
                    return asStream(iterator);
                }
                return emptyStream();
            }
        };
    }

    private Streams() {}
}
