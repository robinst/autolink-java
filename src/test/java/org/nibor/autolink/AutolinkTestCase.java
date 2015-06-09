package org.nibor.autolink;

import static org.junit.Assert.assertEquals;

public abstract class AutolinkTestCase {

    protected void assertLinked(String input, String expected, LinkType expectedLinkType) {
        String result = link(input, "|", expectedLinkType);
        assertEquals(expected, result);
    }

    protected void assertNotLinked(String input) {
        String result = link(input, "|", null);
        assertEquals(input, result);
    }

    protected abstract LinkExtractor getLinkExtractor();

    protected String link(final String input, final String marker, final LinkType expectedLinkType) {
        Iterable<LinkSpan> links = getLinkExtractor().extractLinks(input);
        return Autolink.renderLinks(input, links, new LinkRenderer() {
            @Override
            public void render(LinkSpan link, StringBuilder sb) {
                if (expectedLinkType != null) {
                    assertEquals(expectedLinkType, link.getType());
                }
                sb.append(marker);
                sb.append(input, link.getBeginIndex(), link.getEndIndex());
                sb.append(marker);
            }
        });
    }

}
