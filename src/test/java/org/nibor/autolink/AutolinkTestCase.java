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

    private String link(final String input, final String marker, final LinkType expectedLinkType) {
        return Autolink.renderLinks(input, getLinkExtractor(), new LinkRenderer() {
            @Override
            public void render(Link link, StringBuilder sb) {
                assertEquals(expectedLinkType, link.getType());
                sb.append(marker);
                sb.append(input, link.getBeginIndex(), link.getEndIndex());
                sb.append(marker);
            }
        });
    }

}
