package org.nibor.autolink;

/**
 * Information for an extracted link.
 */
public interface LinkSpan {

    /**
     * @return the type of link
     */
    LinkType getType();

    /**
     * @return begin index (inclusive) in the original input that this link starts at
     */
    int getBeginIndex();

    /**
     * @return end index (exclusive) in the original input that this link ends at; in other words, index of first
     * character after link
     */
    int getEndIndex();

}
