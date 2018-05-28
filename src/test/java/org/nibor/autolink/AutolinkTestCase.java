package org.nibor.autolink;

import static org.junit.Assert.assertEquals;

public abstract class AutolinkTestCase {

    protected void assertLinked(String input, String expected, LinkType expectedLinkType) {
        String result = renderExtractedLinks(input, "|", expectedLinkType);
        assertEquals(expected, result);

        result = renderExtractedSpans(input, "|", expectedLinkType);
        assertEquals(expected, result);

    }

    protected void assertNotLinked(String input) {
        String result = renderExtractedLinks(input, "|", null);
        assertEquals(input, result);

        result = renderExtractedSpans(input, "|", null);
        assertEquals(input, result);
    }

    protected abstract LinkExtractor getLinkExtractor();

    protected String renderExtractedLinks(String input, final String marker, final LinkType expectedLinkType) {
        Iterable<LinkSpan> links = getLinkExtractor().extractLinks(input);
        return Autolink.renderLinks(input, links, new LinkRenderer() {
            @Override
            public void render(LinkSpan link, CharSequence text, StringBuilder sb) {
                if (expectedLinkType != null) {
                    assertEquals(expectedLinkType, link.getType());
                }
                sb.append(marker);
                sb.append(text, link.getBeginIndex(), link.getEndIndex());
                sb.append(marker);
            }
        });
    }

    protected String renderExtractedSpans(String input, final String marker, final LinkType expectedLinkType) {
        Iterable<Span> spans = getLinkExtractor().extractSpans(input);
        StringBuilder sb = new StringBuilder();
        for (Span span : spans) {
            if (span instanceof LinkSpan) {
                LinkType type = ((LinkSpan) span).getType();
                if (expectedLinkType != null) {
                    assertEquals(expectedLinkType, type);
                }
                sb.append(marker);
                sb.append(input, span.getBeginIndex(), span.getEndIndex());
                sb.append(marker);
            } else {
                sb.append(input, span.getBeginIndex(), span.getEndIndex());
            }
        }
        return sb.toString();
    }

}
