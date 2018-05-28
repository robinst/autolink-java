package org.nibor.autolink;

import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class SpanIterableTest {

    @Test
    public void iteratorIsNew() {
        Iterable<Span> iterable = extractSpans("test");
        assertEquals(4, iterable.iterator().next().getEndIndex());
        assertEquals(4, iterable.iterator().next().getEndIndex());
    }

    @Test
    public void hasNextOnlyAdvancesOnce() {
        Iterable<Span> iterable = extractSpans("test");
        Iterator<Span> iterator = iterable.iterator();
        assertTrue(iterator.hasNext());
        assertTrue(iterator.hasNext());
        assertNotNull(iterator.next());
        assertFalse(iterator.hasNext());
        assertFalse(iterator.hasNext());
    }

    @Test
    public void spanAndLinkSequences() {
        assertEquals(Arrays.asList("test"),
                extractSpansAsText("test"));
        assertEquals(Arrays.asList("http://example.org"),
                extractSpansAsText("http://example.org"));
        assertEquals(Arrays.asList("test ", "http://example.org"),
                extractSpansAsText("test http://example.org"));
        assertEquals(Arrays.asList("http://example.org", " test"),
                extractSpansAsText("http://example.org test"));
        assertEquals(Arrays.asList("http://example.org", " ", "https://example.com"),
                extractSpansAsText("http://example.org https://example.com"));
    }

    @Test(expected = NoSuchElementException.class)
    public void nextThrowsNoSuchElementException() {
        Iterable<Span> iterable = extractSpans("test");
        Iterator<Span> iterator = iterable.iterator();
        assertNotNull(iterator.next());
        iterator.next();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void removeUnsupported() {
        Iterable<Span> iterable = extractSpans("test");
        iterable.iterator().remove();
    }

    private Iterable<Span> extractSpans(String input) {
        return LinkExtractor.builder().build().extractSpans(input);
    }

    private List<String> extractSpansAsText(String input) {
        List<String> text = new ArrayList<>();
        for (Span span : extractSpans(input)) {
            text.add(input.substring(span.getBeginIndex(), span.getEndIndex()));
        }
        return text;
    }
}
