package org.nibor.autolink;

import java.util.List;

public class Autolink {

    public static String renderLinks(CharSequence input, LinkExtractor linkExtractor, LinkRenderer linkRenderer) {
        List<Link> links = linkExtractor.getLinks(input);
        StringBuilder sb = new StringBuilder(input.length() + 16);
        int lastIndex = 0;
        for (Link link : links) {
            sb.append(input, lastIndex, link.getBeginIndex());
            linkRenderer.render(link, sb);
            lastIndex = link.getEndIndex();
        }
        if (lastIndex < input.length()) {
            sb.append(input, lastIndex, input.length());
        }
        return sb.toString();
    }

}
