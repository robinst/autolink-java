package org.nibor.autolink.internal;

import org.nibor.autolink.Span;

public class SpanImpl implements Span {

    private final int beginIndex;
    private final int endIndex;

    public SpanImpl(int beginIndex, int endIndex) {
        this.beginIndex = beginIndex;
        this.endIndex = endIndex;
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
        return "Span{beginIndex=" + beginIndex + ", endIndex=" + endIndex + "}";
    }
}
