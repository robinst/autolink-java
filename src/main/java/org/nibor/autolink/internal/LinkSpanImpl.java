package org.nibor.autolink.internal;

import org.nibor.autolink.LinkSpan;
import org.nibor.autolink.LinkType;

public class LinkSpanImpl implements LinkSpan {

    private final LinkType linkType;
    private final int beginIndex;
    private final int endIndex;
    private final CharSequence sequence;

    public LinkSpanImpl(LinkType linkType, int beginIndex, int endIndex, CharSequence sequence) {
        this.linkType = linkType;
        this.beginIndex = beginIndex;
        this.endIndex = endIndex;
        this.sequence = sequence;
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
    public CharSequence sequence() {
        return sequence;
    }

    @Override
    public String toString() {
        return "Link{type=" + getType() + ", beginIndex=" + beginIndex + ", endIndex=" + endIndex + "}";
    }

}
