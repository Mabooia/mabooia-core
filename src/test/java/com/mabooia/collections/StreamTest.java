package com.mabooia.collections;

import com.mabooia.collections.streams.Stream;
import com.mabooia.collections.streams.Streams;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class StreamTest {

    @Test
    public void test_isEmpty() {
        // given
        final Stream<?> stream = Streams.emptyStream();

        // then
        assertTrue(stream.isEmpty());
    }

    @Test
    public void test_append_item() {
        // when
        final Stream<Integer> res = Streams
            .intStartingAt(1)
            .take(10)
            .append(0);

        // then
        assertTrue(res.correspondsTo(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 0));
    }

    @Test
    public void test_append_stream() {
        // when
        final Stream<Integer> res = Streams
            .intStartingAt(1)
            .take(10)
            .append(() -> Streams.asStream(100, 200, 300));

        // then
        assertTrue(res.correspondsTo(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 100, 200, 300));
    }

    @Test
    public void test_castTo() {
        // given
        final Stream<Object> stream = Streams.asStream("abc", "def", "ghi");

        // when
        final Stream<String> res = stream.castTo(String.class);

        // then
        assertTrue(res.correspondsTo("abc", "def", "ghi"));
    }

    @Test
    public void test_filter() {
        // when
        final Stream<Integer> res = Streams
            .intStartingAt(1)
            .filter(n -> n % 2 == 0);

        //then
        assertTrue(res.correspondsTo(2, 4, 6, 8, 10, 12, 14, 16, 18, 20));
    }

    @Test
    public void test_filterNot() {
        // when
        final Stream<Integer> res = Streams
            .intStartingAt(1)
            .filterNot(n -> n % 2 == 0);

        //then
        assertTrue(res.correspondsTo(1, 3, 5, 7, 9, 11, 13, 15, 17, 19, 21));
    }

    @Test
    public void test_flatMap() {
        // given
        final Stream<Integer> naturals = Streams.intStartingAt(1);
        final Function<Integer, Stream<Integer>> multiplesOfN = n -> naturals
            .zip(Streams.repeat(n))
            .map(p -> p.getLeft() * p.getRight());

        // when
        final Stream<Integer> res = naturals
            .flatMap(n -> multiplesOfN.apply(n).take(3));

        // then
        assertTrue(res.correspondsTo(1, 2, 3, 2, 4, 6, 3, 6, 9, 4, 8, 12));
    }

    @Test
    public void test_flatMap2() {
        // given
        final Stream<Integer> naturals = Streams.intStartingAt(1);
        final Function<Integer, Optional<Integer>> getDivisibleByThreeOptionals = n ->
            n % 3 == 0
                ? Optional.of(n)
                : Optional.empty();

        // when
        final Stream<Integer> res = naturals.flatMap2(getDivisibleByThreeOptionals);

        // then
        assertTrue(res.correspondsTo(3, 6, 9, 12, 15, 18, 21));
    }

    @Test
    public void test_map() {
        // when
        final Stream<Integer> res = Streams
            .intStartingAt(1)
            .map(n -> n * 10);

        // then
        assertTrue(res.correspondsTo(10, 20, 30, 40, 50, 60, 70, 80, 90, 100));
    }

    @Test
    public void test_prepend_item() {
        // when
        final Stream<Integer> res = Streams
            .intStartingAt(1)
            .prepend(0);

        // then
        assertTrue(res.correspondsTo(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
    }

    @Test
    public void test_prepend_stream() {
        // when
        final Stream<Integer> res = Streams
            .intStartingAt(1)
            .prepend(Streams.asStream(-2, -1, 0));

        // then
        assertTrue(res.correspondsTo(-2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
    }

    @Test
    public void test_reduce() {
        // given
        final int count = 100;
        final long expected = 5050L;

        // when
        final long sum = Streams
            .intStartingAt(1)
            .take(count)
            .reduce(0L, Long::sum);

        //then
        assertEquals(expected, sum);
    }

    @Test
    public void test_reduceWhile() {
        // given
        final long expected = 45;

        // when
        final long sum = Streams
            .intStartingAt(1)
            .reduceWhile(
                0L,
                (s, n) -> n < 10,
                Long::sum
            );

        //then
        assertEquals(expected, sum);
    }

    @Test
    public void test_reduceWithIndex() {
        // given
        final int count = 10;
        final long expected = 100;

        // when
        final long sum = Streams
            .intStartingAt(1)
            .take(count)
            .reduceWithIndex(0L, (s, n, idx) -> s + n + idx);

        //then
        assertEquals(expected, sum);
    }

    @Test
    public void test_reduceWhileWithIndex() {
        // given
        final long expected = 81;

        // when
        final long sum = Streams
            .intStartingAt(1)
            .reduceWhileWithIndex(
                0L,
                (s, n, idx) -> n < 10,
                (s, n, idx) -> s + n + idx
            );

        //then
        assertEquals(expected, sum);
    }

    @Test
    public void test_take() {
        // when
        final Stream<Integer> res = Streams
            .intStartingAt(1)
            .take(10);

        //then
        assertEquals(10, res.size());
        assertTrue(res.correspondsTo(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
    }

    @Test
    public void test_takeWhile() {
        // when
        final Stream<Integer> res = Streams
            .intStartingAt(1)
            .takeWhile(n -> n < 10);

        //then
        assertEquals(9, res.size());
        assertTrue(res.correspondsTo(1, 2, 3, 4, 5, 6, 7, 8, 9));
    }

    @Test
    public void test_takeWhileNot() {
        // when
        final Stream<Integer> res = Streams
            .intStartingAt(1)
            .takeWhileNot(n -> n >= 10);

        //then
        assertEquals(9, res.size());
        assertTrue(res.correspondsTo(1, 2, 3, 4, 5, 6, 7, 8, 9));
    }

    @Test
    public void test_skip() {
        // when
        final Stream<Integer> res = Streams
            .intStartingAt(1)
            .skip(10);

        //then
        assertTrue(res.correspondsTo(11, 12, 13, 14, 15, 16, 17, 18, 19, 20));
    }

    @Test
    public void test_skipWhile() {
        // when
        final Stream<Integer> res = Streams
            .intStartingAt(1)
            .skipWhile(n -> n < 10);

        //then
        assertTrue(res.correspondsTo(10, 11, 12, 13, 14, 15));
    }

    @Test
    public void test_skipWhileNot() {
        // when
        final Stream<Integer> res = Streams
            .intStartingAt(1)
            .skipWhileNot(n -> n < 10);

        //then
        assertTrue(res.correspondsTo(1, 2, 3, 4, 5, 6, 7, 8, 9));
    }

    @Test
    public void test_toList() {
        // given
        final Stream<Integer> stream = Streams
            .intStartingAt(1)
            .take(5);

        // when
        final List<Integer> list = stream.toList();
        final Iterator<Integer> iterator = list.iterator();

        // then
        assertSame(5, list.size());
        assertSame(1, iterator.next());
        assertSame(2, iterator.next());
        assertSame(3, iterator.next());
        assertSame(4, iterator.next());
        assertSame(5, iterator.next());
    }

    @Test
    public void test_toMap() {
        // given
        final Stream<Integer> stream = Streams
            .intStartingAt(1)
            .take(5);

        // when
        final Map<Integer, String> map = stream.toMap(Function.identity(), Object::toString);

        // then
        assertSame(5, map.size());
        assertEquals("1", map.get(1));
        assertEquals("2", map.get(2));
        assertEquals("3", map.get(3));
        assertEquals("4", map.get(4));
        assertEquals("5", map.get(5));
    }

    @Test
    public void test_toSet() {
        // given
        final Stream<Integer> stream = Streams
            .intStartingAt(1)
            .take(5)
            .append(1);

        // when
        final Set<Integer> set = stream.toSet();
        final Iterator<Integer> iterator = set.iterator();

        // then
        assertSame(5, set.size());
        assertSame(1, iterator.next());
        assertSame(2, iterator.next());
        assertSame(3, iterator.next());
        assertSame(4, iterator.next());
        assertSame(5, iterator.next());
    }

    @Test
    public void test_unzip() {
        // given
        final Stream<Pair<Character, Integer>> composedStream = Streams.asStream(
            Pair.of('a', 1),
            Pair.of('b', 2),
            Pair.of('c', 3),
            Pair.of('d', 4),
            Pair.of('e', 5)
        );

        // when
        final Pair<Stream<Character>, Stream<Integer>> res = composedStream.unzip(Function.identity());

        // then
        assertTrue(res.getLeft().correspondsTo('a', 'b', 'c', 'd', 'e'));
        assertTrue(res.getRight().correspondsTo(1, 2, 3, 4, 5));
    }

    @Test
    public void test_zip() {
        // given
        final Stream<Character> chars = Streams.asStream('a', 'b', 'c', 'd', 'e');
        final Stream<Integer> naturals = Streams.intStartingAt(1);

        // when
        final Stream<Pair<Character, Integer>> res = chars.zip(naturals);

        // then
        assertTrue(res.correspondsTo(
            Pair.of('a', 1),
            Pair.of('b', 2),
            Pair.of('c', 3),
            Pair.of('d', 4),
            Pair.of('e', 5)
        ));
    }

    @Test
    public void test_zipWithIndex() {
        // given
        final Stream<Character> chars = Streams.asStream('a', 'b', 'c', 'd', 'e');

        // when
        final Stream<Pair<Character, Integer>> res = chars.zipWithIndex();

        // then
        assertTrue(res.correspondsTo(
            Pair.of('a', 0),
            Pair.of('b', 1),
            Pair.of('c', 2),
            Pair.of('d', 3),
            Pair.of('e', 4)
        ));
    }
}
