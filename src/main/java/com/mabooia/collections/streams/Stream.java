package com.mabooia.collections.streams;

import com.mabooia.ObjectsEx;
import com.mabooia.ThrowingBiConsumer;
import com.mabooia.ThrowingConsumer;
import com.mabooia.TriFunctionEx;
import com.mabooia.TriPredicate;
import com.mabooia.collections.streams.impl.parallel.ParallelStreamOperations;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public interface Stream<A> extends Iterable<A> {

    Optional<A> getHeadIfPresent();

    Stream<A> getTail();

    default boolean isEmpty() {
        return !nonEmpty();
    }

    default boolean nonEmpty() {
        return getHeadIfPresent().isPresent();
    }

    default boolean allMatch(final Predicate<A> p) {
        Stream<A> curr = this;
        Optional<A> currHead;
        while ((currHead = curr.getHeadIfPresent()).isPresent()) {
            if (!p.test(currHead.get())) {
                return false;
            }
            curr = curr.getTail();
        }
        return true;
    }

    default Stream<A> append(final A item) {
        return Streams
            .of(() -> getHeadIfPresent()
                .map(head -> Streams.of(head, () -> getTail().append(item)))
                .orElseGet(() -> Streams.singleton(item))
            ) ;
    }

    default Stream<A> append(final Stream<A> stream) {
        return Streams
            .of(() -> getHeadIfPresent()
                .map(head -> Streams.of(head, () -> getTail().append(stream)))
                .orElse(stream)
            );
    }

    default Stream<A> append(final Supplier<Stream<A>> supplier) {
        return Streams
            .of(() -> getHeadIfPresent()
                .map(head -> Streams.of(head, () -> getTail().append(supplier)))
                .orElseGet(supplier)
            );
    }

    default <B extends A> Stream<B> castTo(final Class<B> type) {
        return map(it -> ObjectsEx.as(type, it));
    }

    default <B> boolean correspondsTo(final B...array) {
        return correspondsTo(Streams.asStream(array));
    }

    default <B> boolean correspondsTo(final Stream<B> other) {
        return correspondsTo(other, Objects::equals);
    }

    default <B> boolean correspondsTo(final Stream<B> other, final BiPredicate<? super A, ? super B> p) {
        return zip(other)
            .allMatch(pair -> p.test(pair.getLeft(), pair.getRight()));
    }

    default Stream<A> filter(final Predicate<? super A> p) {
        return Streams.filterIf(this, p, true);
    }

    default Stream<A> filterNot(final Predicate<? super A> p) {
        return Streams.filterIf(this, p, false);
    }

    default <B> Stream<B> flatMap(final Function<? super A, Stream<B>> f) {
        return Streams.of(() -> getHeadIfPresent()
            .map(head -> f.apply(head).append(getTail().flatMap(f)))
            .orElseGet(Streams::emptyStream)
        );
    }

    default <B> Stream<B> flatMap2(final Function<? super A, Optional<B>> f) {
        return map(f)
            .filter(Optional::isPresent)
            .map(Optional::get);
    }

    default <B> Stream<B> flatMap3(final Function<? super A, ? extends Iterable<B>> f) {
        return flatMap(it -> Streams.asStream(f.apply(it)));
    }

    default void forEach(final Consumer<? super A> action) {
        Stream<A> curr = this;
        Optional<A> currHead;
        while ((currHead = curr.getHeadIfPresent()).isPresent()) {
            action.accept(currHead.get());
            curr = curr.getTail();
        }
    }

    default Stream<A> forEachAndGet(final Consumer<? super A> action) {
        forEach(action);
        return this;
    }

    default <E extends Exception> void forEachEx(final ThrowingConsumer<? super A, E> action) throws E {
        Stream<A> curr = this;
        Optional<A> currHead;
        while ((currHead = curr.getHeadIfPresent()).isPresent()) {
            action.accept(currHead.get());
            curr = curr.getTail();
        }
    }

    default <E extends Exception> Stream<A> forEachExAndGet(final ThrowingConsumer<? super A, E> action) throws E {
        forEachEx(action);
        return this;
    }

    default void forEachWithIndex(final BiConsumer<? super A, Long> action) {
        forEachExWithIndex(action::accept);
    }

    default Stream<A> forEachWithIndexAndGet(final BiConsumer<? super A, Long> action) {
        forEachWithIndex(action);
        return this;
    }

    default <E extends Exception> void forEachExWithIndex(final ThrowingBiConsumer<? super A, Long, E> action)
        throws E {
        Stream<A> curr = this;
        Optional<A> currHead;
        long idx = 0;
        while ((currHead = curr.getHeadIfPresent()).isPresent()) {
            action.accept(currHead.get(), idx);
            curr = curr.getTail();
            idx++;
        }
    }

    default <E extends Exception> Stream<A> forEachExWithIndexAndGet(final ThrowingBiConsumer<? super A, Long, E> action)
        throws E {
        forEachExWithIndex(action);
        return this;
    }

    default void forEachWhile(final Predicate<? super A> p, final Consumer<? super A> action) {
        forEachWhileIf(true, p, action);
    }

    default Stream<A> forEachWhileAndGet(final Predicate<? super A> p, final Consumer<? super A> action) {
        forEachWhile(p, action);
        return this;
    }

    default void forEachWhileNot(final Predicate<A> p, final Consumer<? super A> action) {
        forEachWhileIf(false, p, action);
    }

    default Stream<A> forEachWhileNotAndGet(final Predicate<A> p, final Consumer<? super A> action) {
        forEachWhileNot(p, action);
        return this;
    }

    default void forEachWhileIf(boolean isTrue, final Predicate<? super A> p, final Consumer<? super A> action) {
        reduceWhile(
            0,
            (ignored, it) -> p.test(it) == isTrue,
            (ignored, it) -> {
                action.accept(it);
                return 0;
            }
        );
    }

    default Iterator<A> iterator() {
        final Stream<A> source = this;

        return new Iterator<>() {
            private Stream<A> current = source;

            @Override
            public boolean hasNext() {
                return current.nonEmpty();
            }

            @Override
            public A next() {
                final Optional<A> head = current.getHeadIfPresent();
                if (head.isPresent()) {
                    current = current.getTail();
                    return head.get();
                }

                throw new NoSuchElementException();
            }
        };
    }

    default <B> Stream<B> map(final Function<? super A, B> f) {
        return Streams.map(this, f);
    }

    default ParallelStreamOperations<A> parallel() {
        return new ParallelStreamOperations<>(this);
    }

    default Stream<A> prepend(final A item) {
        return Streams.of(item, this);
    }

    default Stream<A> prepend(final Stream<A> stream) {
        return stream.append(this);
    }

    default <B> B reduce(final B initialValue, final BiFunction<? super B, ? super A, B> f) {
        B result = initialValue;
        Stream<A> curr = this;
        Optional<A> currHead;
        while ((currHead = curr.getHeadIfPresent()).isPresent()) {
            result = f.apply(result, currHead.get());
            curr = curr.getTail();
        }
        return result;
    }

    default <B> B reduceWithIndex(final B initialValue, final TriFunctionEx<? super B, ? super A, Long, B> f) {
        B result = initialValue;
        Stream<A> curr = this;
        Optional<A> currHead;
        long idx = 0;
        while ((currHead = curr.getHeadIfPresent()).isPresent()) {
            result = f.apply(result, currHead.get(), idx);
            curr = curr.getTail();
            idx++;
        }
        return result;
    }

    default <B> B reduceWhile(final B initialValue,
                              final BiPredicate<? super B, ? super A> p,
                              final BiFunction<? super B, ? super A, B> f) {
        B result = initialValue;
        Stream<A> curr = this;
        Optional<A> currHead;
        while ((currHead = curr.getHeadIfPresent()).isPresent() && p.test(result, currHead.get())) {
            result = f.apply(result, currHead.get());
            curr = curr.getTail();
        }
        return result;
    }

    default <B> B reduceWhileWithIndex(final B initialValue,
                                       final TriPredicate<? super B, ? super A, Long> p,
                                       final TriFunctionEx<? super B, ? super A, Long, B> f) {
        B result = initialValue;
        Stream<A> curr = this;
        Optional<A> currHead;
        long idx = 0;
        while ((currHead = curr.getHeadIfPresent()).isPresent() && p.test(result, currHead.get(), idx)) {
            result = f.apply(result, currHead.get(), idx);
            curr = curr.getTail();
            idx++;
        }
        return result;
    }

    default long size() {
        return reduce(0L, (s, it) -> s + 1);
    }

    default Stream<A> skip(final int count) {
        return Streams.skipWhileWithIndexIf(this, (it, idx) -> idx < count, true);
    }

    default Stream<A> skipWhile(final Predicate<? super A> p) {
        return Streams.skipWhileIf(this, p, true);
    }

    default Stream<A> skipWhileNot(final Predicate<? super A> p) {
        return Streams.skipWhileIf(this, p, false);
    }

    default Stream<A> take(final int count){
        return Streams.takeWhileWithIndexIf(this, 0, (it, idx) -> idx < count, true);
    }

    default Stream<A> takeWhile(final Predicate<? super A> p) {
        return Streams.takeWhileIf(this, p, true);
    }

    default Stream<A> takeWhileNot(final Predicate<? super A> p) {
        return Streams.takeWhileIf(this, p, false);
    }

    default List<A> toList() {
        final List<A> list = new LinkedList<>();
        forEach(list::add);
        return list;
    }

    default <K, V> Map<K, V> toMap(final Function<? super A, K> getKey,
                                   final Function<? super A, V> getValue) {
        final Stream<Pair<K, V>> entries = map(it -> Pair.of(
            getKey.apply(it),
            getValue.apply(it)
        ));

        return Streams.toMap(entries);
    }

    default Set<A> toSet() {
        final Set<A> set = new HashSet<>();
        forEach(set::add);
        return set;
    }

    default <B, C> Pair<Stream<B>, Stream<C>> unzip(final Function<? super A, Pair<B, C>> f) {
        final Stream<Pair<B, C>> streamOfPair = map(f);
        return Pair.of(
            streamOfPair.map(Pair::getLeft),
            streamOfPair.map(Pair::getRight)
        );
    }

    default <B> Stream<Pair<A, B>> zip(final Stream<B> other) {
        return Streams.zip(this, other);
    }

    default Stream<Pair<A, Integer>> zipWithIndex() {
        return zip(Streams.intStartingAt(0));
    }
}

