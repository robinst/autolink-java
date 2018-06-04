package org.nibor.autolink;

/**
 * Information for an extracted link.
 */
public interface LinkSpan extends Span {

    /**
     * @return the type of link
     */
    LinkType getType();

}
