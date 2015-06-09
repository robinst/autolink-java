package org.nibor.autolink;

import org.junit.Test;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.junit.Assert.*;

public class LinkExtractorIterableTest {

    @Test
    public void iteratorIsNew() {
        Iterable<Link> iterable = getSingleElementIterable();
        assertEquals(LinkType.URL, iterable.iterator().next().getType());
        assertEquals(LinkType.URL, iterable.iterator().next().getType());
    }

    @Test
    public void hasNextOnlyAdvancesOnce() {
        Iterable<Link> iterable = getSingleElementIterable();
        Iterator<Link> iterator = iterable.iterator();
        assertTrue(iterator.hasNext());
        assertTrue(iterator.hasNext());
        assertNotNull(iterator.next());
        assertFalse(iterator.hasNext());
        assertFalse(iterator.hasNext());
    }

    @Test(expected = NoSuchElementException.class)
    public void nextThrowsNoSuchElementException() {
        Iterable<Link> iterable = getSingleElementIterable();
        Iterator<Link> iterator = iterable.iterator();
        assertNotNull(iterator.next());
        iterator.next();
    }

    private Iterable<Link> getSingleElementIterable() {
        String input = "foo http://example.com";
        return LinkExtractor.builder().build().extractLinks(input);
    }
}
