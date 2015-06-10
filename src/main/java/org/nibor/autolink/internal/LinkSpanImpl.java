package org.nibor.autolink.internal;

import org.nibor.autolink.LinkSpan;
import org.nibor.autolink.LinkType;

public class LinkSpanImpl implements LinkSpan {

    private final LinkType linkType;
    private final int beginIndex;
    private final int endIndex;

    public LinkSpanImpl(LinkType linkType, int beginIndex, int endIndex) {
        this.linkType = linkType;
        this.beginIndex = beginIndex;
        this.endIndex = endIndex;
    }

    @Override
    public LinkType getType() {
        return linkType;
    }

    @Override
    public int getBeginIndex() {
        return beginIndex;
    }

    @Override
    public int getEndIndex() {
        return endIndex;
    }

    @Override
    public String toString() {
        return "Link{type=" + getType() + ", beginIndex=" + beginIndex + ", endIndex=" + endIndex + "}";
    }

}
