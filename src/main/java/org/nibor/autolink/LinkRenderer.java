package org.nibor.autolink;

/**
 * Renderer for a link
 *
 * @deprecated use {@link LinkExtractor#extractSpans(CharSequence)} instead.
 */
@Deprecated
public interface LinkRenderer {

    /**
     * Render the supplied link of the input text to the supplied output.
     *
     * @param link the link span of the link to render
     * @param input the input text where the link occurs
     * @param output the output to write the link to
     */
    void render(LinkSpan link, CharSequence input, StringBuilder output);

}
