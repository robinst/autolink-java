package org.nibor.autolink;

/**
 * Utility class for processing text with links.
 */
public class Autolink {

    /**
     * Render the supplied links from the supplied input text using a renderer. The parts of the text outside of links
     * are added to the result without processing.
     *
     * @param input the input text
     * @param links the links to render, see {@link LinkExtractor} to extract them
     * @param linkRenderer the link rendering implementation
     * @return the rendered string
     */
    public static String renderLinks(CharSequence input, Iterable<LinkSpan> links, LinkRenderer linkRenderer) {
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
