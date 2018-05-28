package org.nibor.autolink;

/**
 * Utility class for processing text with links.
 */
public class Autolink {

    /**
     * Render the supplied links from the supplied input text using a renderer. The parts of the text outside of links
     * are added to the result without processing.
     *
     * @param input the input text, must not be null
     * @param links the links to render, see {@link LinkExtractor} to extract them
     * @param linkRenderer the link rendering implementation
     * @return the rendered string
     * @deprecated use {@link LinkExtractor#extractSpans(CharSequence)} instead
     */
    @Deprecated
    public static String renderLinks(CharSequence input, Iterable<LinkSpan> links, LinkRenderer linkRenderer) {
        if (input == null) {
            throw new NullPointerException("input must not be null");
        }
        if (links == null) {
            throw new NullPointerException("links must not be null");
        }
        if (linkRenderer == null) {
            throw new NullPointerException("linkRenderer must not be null");
        }
        StringBuilder sb = new StringBuilder(input.length() + 16);
        int lastIndex = 0;
        for (LinkSpan link : links) {
            sb.append(input, lastIndex, link.getBeginIndex());
            linkRenderer.render(link, input, sb);
            lastIndex = link.getEndIndex();
        }
        if (lastIndex < input.length()) {
            sb.append(input, lastIndex, input.length());
        }
        return sb.toString();
    }

}
