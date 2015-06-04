package org.nibor.autolink;

import org.nibor.autolink.internal.EmailScanner;
import org.nibor.autolink.internal.Scanner;
import org.nibor.autolink.internal.UrlScanner;

import java.util.*;

/**
 * Extracts links from input.
 * <p>
 * Create and configure an extractor using {@link #builder()}.
 */
public class LinkExtractor {

    private static Scanner URL_SCANNER = new UrlScanner();
    private static Scanner EMAIL_SCANNER = new EmailScanner();

    private final Set<LinkType> linkTypes;

    private LinkExtractor(Set<LinkType> linkTypes) {
        this.linkTypes = linkTypes;
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Extract the links from the input. Can be called multiple times with different inputs, is thread-safe.
     *
     * @param input the input text, must not be {@code null}
     * @return the links, in order that they appear in the input, never {@code null}
     */
    public List<Link> getLinks(CharSequence input) {
        List<Link> links = new ArrayList<>();

        int rewindIndex = 0;
        int[] result = new int[2];

        int i = 0;
        int length = input.length();
        while (i < length) {
            Scanner scanner = trigger(input.charAt(i));
            if (scanner != null) {
                boolean found = scanner.scan(input, i, rewindIndex, result);
                if (found) {
                    links.add(new LinkImpl(scanner.getLinkType(), result[0], result[1]));
                    rewindIndex = result[1];
                    i = result[1];
                } else {
                    i++;
                }
            } else {
                i++;
            }
        }
        return links;
    }

    private Scanner trigger(char c) {
        switch (c) {
            case ':':
                if (linkTypes.contains(LinkType.URL)) {
                    return URL_SCANNER;
                }
                break;
            case '@':
                if (linkTypes.contains(LinkType.EMAIL)) {
                    return EMAIL_SCANNER;
                }
        }
        return null;
    }

    /**
     * Builder for configuring link extractor.
     */
    public static class Builder {

        private Set<LinkType> linkTypes = EnumSet.allOf(LinkType.class);

        private Builder() {
        }

        /**
         * @param linkTypes the link types that should be extracted (by default, all types are extracted)
         * @return this builder
         */
        public Builder linkTypes(Set<LinkType> linkTypes) {
            this.linkTypes = Objects.requireNonNull(linkTypes, "linkTypes must not be null");
            return this;
        }

        /**
         * @return the configured link extractor
         */
        public LinkExtractor build() {
            return new LinkExtractor(linkTypes);
        }
    }

    private static class LinkImpl implements Link {

        private final LinkType linkType;
        private final int beginIndex;
        private final int endIndex;

        private LinkImpl(LinkType linkType, int beginIndex, int endIndex) {
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

}
