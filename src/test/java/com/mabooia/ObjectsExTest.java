package com.mabooia;

import static com.mabooia.ObjectsEx.as;
import static com.mabooia.ObjectsEx.asArrayOf;
import static com.mabooia.ObjectsEx.asMapOf;
import static com.mabooia.ObjectsEx.is;
import static com.mabooia.ObjectsEx.isArrayOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

public class ObjectsExTest {

    @Test
    public void testAsReturnsCastedType() {
        // given
        final Object obj = "sample";

        // when
        final String str = as(String.class, obj);

        // then
        assertNotNull(str);
        assertSame(obj, str);
    }

    @Test
    public void testAsReturnsNull() {
        // given
        final Object obj = "sample";

        // when
        final Integer integer = as(Integer.class, obj);

        // then
        assertNull(integer);
    }

    @Test
    public void testIsType() {
        // given
        final Object obj = "sample";

        // then
        assertTrue(is(String.class, obj));
    }

    @Test
    public void testIsNotType() {
        // given
        final Object obj = "sample";

        // then
        assertFalse(is(Integer.class, obj));
    }

    @Test
    public void testFlexibleAsArrayOfReturnsCastedArray() {
        // given
        final Object obj = new Object[] {
            "str1",
            "str2",
            10,
            "str3",
            null,
        };

        // when
        final String[] array = asArrayOf(String.class, false, obj);

        // then
        assertNotNull(array);
        assertEquals(3, array.length);
        assertEquals("str1", array[0]);
        assertEquals("str2", array[1]);
        assertEquals("str3", array[2]);
    }

    @Test
    public void testStrictAsArrayOfReturnsCastedArray() {
        // given
        final Object obj = new Object[] {
            "str1",
            "str2",
            "str3",
        };

        // when
        final String[] array = asArrayOf(String.class, true, obj);

        // then
        assertNotNull(array);
        assertEquals(3, array.length);
        assertEquals("str1", array[0]);
        assertEquals("str2", array[1]);
        assertEquals("str3", array[2]);
    }

    @Test
    public void testStrictAsArrayOfReturnsNull() {
        // given
        final Object obj = new Object[] {
            "str1",
            "str2",
            10,
            "str3",
            null,
        };

        // when
        final String[] array = asArrayOf(String.class, true, obj);

        // then
        assertNull(array);
    }

    @Test
    public void testFlexibleAsMapOfReturnsCastedMap() {
        // given
        final Map<Object, Object> map = new HashMap<>();
        map.put(10, "str1");
        map.put(20, "str2");
        map.put(25, 10);
        map.put(30, "str3");
        map.put('a', "str4");
        map.put(50, null);

        // when
        final Map<Integer, String> castedMap = asMapOf(Integer.class, String.class, false, map);

        // then
        assertNotNull(castedMap);
        assertEquals(3, castedMap.size());
        assertFalse(castedMap.containsKey(25));
        assertFalse(castedMap.containsKey(50));
        assertEquals("str1", castedMap.get(10));
        assertEquals("str2", castedMap.get(20));
        assertEquals("str3", castedMap.get(30));
    }

    @Test
    public void testStrictAsMapOfReturnsCastedMap() {
        // given
        final Map<Object, Object> map = new HashMap<>();
        map.put(10, "str1");
        map.put(20, "str2");
        map.put(30, "str3");
        map.put(40, "str4");
        map.put(50, "str5");

        // when
        final Map<Integer, String> castedMap = asMapOf(Integer.class, String.class, true, map);

        // then
        assertNotNull(castedMap);
        assertEquals(5, castedMap.size());
        assertEquals("str1", castedMap.get(10));
        assertEquals("str2", castedMap.get(20));
        assertEquals("str3", castedMap.get(30));
        assertEquals("str4", castedMap.get(40));
        assertEquals("str5", castedMap.get(50));
    }

    @Test
    public void testStrictAsMapOfReturnsNull() {
        // given
        final Map<Object, Object> map = new HashMap<>();
        map.put(10, "str1");
        map.put(20, "str2");
        map.put(25, 10);
        map.put(30, "str3");
        map.put('a', "str4");
        map.put(50, null);

        // when
        final Map<Integer, String> castedMap = asMapOf(Integer.class, String.class, true, map);

        // then
        assertNull(castedMap);
    }

    @Test
    public void testIsArrayOf() {
        // given
        final Object obj = new String[] {
            "str1",
            "str2",
            "str3",
        };

        // then
        assertTrue(isArrayOf(String.class, obj));
    }

    @Test
    public void testIsNotArrayOf() {
        // given
        final Object obj = new String[] {
            "str1",
            "str2",
            "str3",
        };

        // then
        assertFalse(isArrayOf(Integer.class, obj));
    }
}
