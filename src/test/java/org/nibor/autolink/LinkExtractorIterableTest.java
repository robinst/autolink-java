package org.nibor.autolink;

import org.junit.Test;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.junit.Assert.*;

public class LinkExtractorIterableTest {

    @Test
    public void iteratorIsNew() {
        Iterable<LinkSpan> iterable = getSingleElementIterable();
        assertEquals(LinkType.URL, iterable.iterator().next().getType());
        assertEquals(LinkType.URL, iterable.iterator().next().getType());
    }

    @Test
    public void hasNextOnlyAdvancesOnce() {
        Iterable<LinkSpan> iterable = getSingleElementIterable();
        Iterator<LinkSpan> iterator = iterable.iterator();
        assertTrue(iterator.hasNext());
        assertTrue(iterator.hasNext());
        assertNotNull(iterator.next());
        assertFalse(iterator.hasNext());
        assertFalse(iterator.hasNext());
    }

    @Test(expected = NoSuchElementException.class)
    public void nextThrowsNoSuchElementException() {
        Iterable<LinkSpan> iterable = getSingleElementIterable();
        Iterator<LinkSpan> iterator = iterable.iterator();
        assertNotNull(iterator.next());
        iterator.next();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void removeUnsupported() {
        Iterable<LinkSpan> iterable = getSingleElementIterable();
        iterable.iterator().remove();
    }

    private Iterable<LinkSpan> getSingleElementIterable() {
        String input = "foo http://example.com";
        return LinkExtractor.builder().build().extractLinks(input);
    }
}
