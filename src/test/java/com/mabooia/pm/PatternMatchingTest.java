package com.mabooia.pm;

import static com.mabooia.pm.patterns.Patterns.eq;
import static com.mabooia.pm.patterns.Patterns.nul;
import static com.mabooia.pm.patterns.Patterns.ofType;
import static com.mabooia.pm.patterns.Patterns.pair;
import static com.mabooia.pm.patterns.Patterns.succ;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import com.mabooia.Try;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

public class PatternMatchingTest {

    @Test(expected = NotMatchingPatternFoundException.class)
    public void testGetWithoutStatementsThrowsException() {
        // given
        final Object str = "some text";

        // then
        Match.when(str)
            .get();
    }

    @Test
    public void testOrElseWithoutStatementsReturnsOrElseResult() {
        // given
        final Object str = "some text";

        // when
        final Object res = Match
            .when(str)
            .orElse(() -> 10);

        // then
        assertSame(10, res);
    }

    @Test
    public void testIsEqualsStatementWhenExprMatches() {
        // given
        final Object str = "some text";

        // then
        assertSame(
            10,
            Match
                .when(str)
                .is(eq("some text")).then(() -> 10)
                .orElse(() -> 20)
        );
        assertSame(
            10,
            Match
                .when(str)
                .is(eq("some text")).then(() -> 10)
                .get()
        );
    }

    @Test
    public void testIsNullStatementsWhenExprMatches() {
        assertSame(
            10,
            Match
                .when(null)
                .is(nul()).then(() -> 10)
                .orElse(() -> 20)
        );
        assertSame(
            10,
            Match
                .when(null)
                .is(nul()).then(() -> 10)
                .get()
        );
    }

    @Test
    public void testIsEqualsStatementWhenExprDoesNotMatch() {
        // given
        final Object str = "some text";

        // then
        assertSame(
            20,
            Match
                .when(str)
                .is(eq("another text")).then(() -> 10)
                .orElse(() -> 20)
        );
    }

    @Test
    public void testIsClassPatternIsTrue() {
        // given
        final Object str = "some text";

        // then
        assertSame(
            9, // length of str
            Match
                .when(str)
                .is(ofType(String.class)).then(String::length)
                .orElse(() -> 20)
        );
    }

    @Test
    public void testIsClassPatternIsFalse() {
        // given
        final Object str = "some text";

        // then
        assertSame(
            20, // or else
            Match
                .when(str)
                .is(ofType(Integer.class)).then(v -> v + 1)
                .orElse(() -> 20)
        );
    }

    @Test
    public void testIsClassAndPatternIsTrue() {
        // given
        final Object str = "some text";

        // then
        assertSame(
            9, // length of str
            Match
                .when(str)
                .is(ofType(String.class)).and(s -> s.startsWith("some")).then(String::length)
                .orElse(() -> 20)
        );
        assertSame(
            9, // length of str
            Match
                .when(str)
                .is(ofType(String.class)).and(s -> s.startsWith("some")).then(String::length)
                .get()
        );
    }

    @Test
    public void testIsClassAndPatternIsFalse() {
        // given
        final Object str = "some text";

        // then
        assertSame(
            20, // orElse
            Match
                .when(str)
                .is(ofType(String.class)).and(s -> s.startsWith("hello")).then(String::length)
                .orElse(() -> 20)
        );
    }

    @Test
    public void testIsSuccessClassPatternIsTrue() {
        // given
        final Object str = Try.success("some text");

        // then
        assertSame(
            9,
            Match
                .when(str)
                .is(succ(ofType(String.class))).then(String::length)
                .orElse(() -> 20)
        );
    }

    @Test
    public void testIsSuccessClassPatternIsFalse() {
        // given
        final Object str = Try.success("some text");

        // then
        assertSame(
            20,
            Match
                .when(str)
                .is(succ(ofType(Integer.class))).then(a -> 10)
                .orElse(() -> 20)
        );
    }

    @Test
    public void testIsSuccessPatternIsTrue() {
        // given
        final Object str = Try.success("some text");

        // then
        assertSame(
            10,
            Match
                .when(str)
                .is(succ(eq("some text"))).then(() -> 10)
                .orElse(() -> 20)
        );
    }

    @Test
    public void testIsSuccessPatternIsFalse() {
        // given
        final Object str = Try.success("some text");

        // then
        assertSame(
            20,
            Match
                .when(str)
                .is(succ(eq("another text"))).then(() -> 10)
                .orElse(() -> 20)
        );
    }

    @Test
    public void testIsTupleClassClassPatternIsTrue() {
        // given
        final Object tuple = Pair.of("some text", 100);

        // then
        assertEquals(
            "some text + 100",
            Match
                .when(tuple)
                .is(pair(ofType(String.class), ofType(Integer.class))).then((l, r) -> l + " + " + r)
                .orElse(() -> "or else text")
        );
    }

    @Test
    public void testIsTupleClassClassPatternIsFalse() {
        // given
        final Object tuple = Pair.of("some text", 100);

        // then
        assertEquals(
            "or else text",
            Match
                .when(tuple)
                .is(pair(ofType(String.class), ofType(Long.class))).then((l, r) -> l + " + " + r)
                .orElse(() -> "or else text")
        );
    }

    @Test
    public void testIsTupleClassValuePatternIsTrue() {
        // given
        final Object tuple = Pair.of("some text", 100);

        // then
        assertEquals(
            "some text",
            Match
                .when(tuple)
                .is(pair(ofType(String.class), eq(100))).then(s -> s)
                .orElse(() -> "or else text")
        );
    }

    @Test
    public void testIsTupleClassValuePatternIsFalse() {
        // given
        final Object tuple = Pair.of("some text", 100);

        // then
        assertEquals(
            "or else text",
            Match
                .when(tuple)
                .isTuple(String.class, 0).then(s -> s)
                .orElse(() -> "or else text")
        );
        assertEquals(
            "or else text",
            Match
                .when(tuple)
                .isTuple(Integer.class, 100).then(v -> v)
                .orElse(() -> "or else text")
        );
        assertEquals(
            "or else text",
            Match
                .when(tuple)
                .isTuple(Integer.class, 0).then(v -> v)
                .orElse(() -> "or else text")
        );
    }

    @Test
    public void testIsTupleValueClassPatternIsTrue() {
        // given
        final Object tuple = Pair.of("some text", 100);

        // then
        assertEquals(
            100,
            Match
                .when(tuple)
                .isTuple("some text", Integer.class).then(v -> v)
                .orElse(() -> 0)
        );
    }

    @Test
    public void testIsTupleValueClassPatternIsFalse() {
        // given
        final Object tuple = Pair.of("some text", 100);

        // then
        assertEquals(
            0,
            Match
                .when(tuple)
                .isTuple("some other text", Integer.class).then(v -> v)
                .orElse(() -> 0)
        );
        assertEquals(
            "or else text",
            Match
                .when(tuple)
                .isTuple("some text", String.class).then(s -> s)
                .orElse(() -> "or else text")
        );
        assertEquals(
            "or else text",
            Match
                .when(tuple)
                .isTuple("some other text", String.class).then(s -> s)
                .orElse(() -> "or else text")
        );
    }

    @Test
    public void testIsTupleValueValuePatternIsTrue() {
        // given
        final Object tuple = Pair.of("some text", 100);

        // then
        assertEquals(
            "then text",
            Match
                .when(tuple)
                .isTuple("some text", 100).then(() -> "then text")
                .orElse(() -> "or else text")
        );
    }

    @Test
    public void testIsTupleValueValuePatternIsFalse() {
        // given
        final Object tuple = Pair.of("some text", 100);

        // then
        assertEquals(
            "or else text",
            Match
                .when(tuple)
                .isTuple("some other text", 100).then(() -> "then text")
                .orElse(() -> "or else text")
        );
        assertEquals(
            "or else text",
            Match
                .when(tuple)
                .isTuple("some text", 0).then(() -> "then text")
                .orElse(() -> "or else text")
        );
        assertEquals(
            "or else text",
            Match
                .when(tuple)
                .isTuple("some other text", 0).then(() -> "then text")
                .orElse(() -> "or else text")
        );
    }
}
