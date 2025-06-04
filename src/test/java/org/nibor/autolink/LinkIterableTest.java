package org.nibor.autolink;

import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

public class LinkIterableTest {

    @Test
    public void iteratorIsNew() {
        Iterable<LinkSpan> iterable = getSingleLinkIterable();
        assertEquals(LinkType.URL, iterable.iterator().next().getType());
        assertEquals(LinkType.URL, iterable.iterator().next().getType());
    }

    @Test
    public void hasNextOnlyAdvancesOnce() {
        Iterable<LinkSpan> iterable = getSingleLinkIterable();
        Iterator<LinkSpan> iterator = iterable.iterator();
        assertTrue(iterator.hasNext());
        assertTrue(iterator.hasNext());
        assertNotNull(iterator.next());
        assertFalse(iterator.hasNext());
        assertFalse(iterator.hasNext());
    }

    @Test
    public void nextThrowsNoSuchElementException() {
        Iterable<LinkSpan> iterable = getSingleLinkIterable();
        Iterator<LinkSpan> iterator = iterable.iterator();
        assertNotNull(iterator.next());
        assertThrows(NoSuchElementException.class, iterator::next);
    }

    @Test
    public void removeUnsupported() {
        Iterable<LinkSpan> iterable = getSingleLinkIterable();
        assertThrows(UnsupportedOperationException.class, () -> iterable.iterator().remove());
    }

    private Iterable<LinkSpan> getSingleLinkIterable() {
        String input = "foo http://example.com";
        return LinkExtractor.builder().build().extractLinks(input);
    }
}
