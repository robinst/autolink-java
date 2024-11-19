package org.nibor.autolink;

import org.junit.Test;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

public class UsageExampleTest {

    @Test
    public void linkify() {
        String input = "wow http://test.com such linked";
        LinkExtractor linkExtractor = LinkExtractor.builder()
                .linkTypes(EnumSet.of(LinkType.URL)) // limit to URLs
                .build();
        Iterable<Span> spans = linkExtractor.extractSpans(input);

        StringBuilder sb = new StringBuilder();
        for (Span span : spans) {
            String text = input.substring(span.getBeginIndex(), span.getEndIndex());
            if (span instanceof LinkSpan) {
                // span is a URL
                sb.append("<a href=\"");
                sb.append(Encode.forHtmlAttribute(text));
                sb.append("\">");
                sb.append(Encode.forHtml(text));
                sb.append("</a>");
            } else {
                // span is plain text before/after link
                sb.append(Encode.forHtml(text));
            }
        }

        assertEquals("wow <a href=\"http://test.com\">http://test.com</a> such linked", sb.toString());
    }

    @Test
    public void extractLinks() {
        var input = "hey https://test.com and https://example.com";
        var linkExtractor = LinkExtractor.builder()
                .linkTypes(EnumSet.of(LinkType.URL))
                .build();
        var links = new ArrayList<>();
        for (var linkSpan : linkExtractor.extractLinks(input)) {
            var link = input.substring(linkSpan.getBeginIndex(), linkSpan.getEndIndex());
            links.add(link);
        }

        assertEquals(List.of("https://test.com", "https://example.com"), links);
    }

    // Mocked here to not have to depend on owasp-java-encoder in tests
    private static class Encode {
        public static String forHtmlAttribute(String text) {
            return text;
        }

        public static String forHtml(String text) {
            return text;
        }
    }
}
