package org.nibor.autolink;

import org.nibor.autolink.internal.*;
import org.nibor.autolink.internal.Scanner;

import java.util.*;

/**
 * Extracts links from input.
 * <p>
 * Create and configure an extractor using {@link #builder()}, then call {@link #extractLinks}.
 */
public class LinkExtractor {

    private final Scanner urlScanner;
    private final Scanner wwwScanner;
    private final Scanner emailScanner;
    private final Scanner hashtagScanner;

    private LinkExtractor(UrlScanner urlScanner, WwwScanner wwwScanner, EmailScanner emailScanner, HashtagScanner
            hashtagScanner) {
        this.urlScanner = urlScanner;
        this.wwwScanner = wwwScanner;
        this.emailScanner = emailScanner;
        this.hashtagScanner = hashtagScanner;
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Extract the links from the input text. Can be called multiple times with different inputs (thread-safe).
     *
     * @param input the input text, must not be {@code null}
     * @return a lazy iterable for the links in order that they appear in the input, never {@code null}
     */
    public Iterable<LinkSpan> extractLinks(final CharSequence input) {
        return new Iterable<LinkSpan>() {
            @Override
            public Iterator<LinkSpan> iterator() {
                return new LinkIterator(input);
            }
        };
    }

    private Scanner trigger(char c) {
        switch (c) {
            case ':':
                return urlScanner;
            case '@':
                return emailScanner;
            case 'w':
                return wwwScanner;
            case '#':
                return hashtagScanner;
        }
        return null;
    }

    /**
     * Builder for configuring link extractor.
     */
    public static class Builder {

        private Set<LinkType> linkTypes = EnumSet.allOf(LinkType.class);
        private boolean emailDomainMustHaveDot = true;
        private Set<Character> allowedHashtagSpecialChars = null;

        private Builder() {
        }

        /**
         * @param linkTypes the link types that should be extracted (by default, all types are extracted)
         * @return this builder
         */
        public Builder linkTypes(Set<LinkType> linkTypes) {
            if (linkTypes == null) {
                throw new NullPointerException("linkTypes must not be null");
            }
            this.linkTypes = new HashSet<>(linkTypes);
            return this;
        }

        /**
         * @param emailDomainMustHaveDot true if the domain in an email address is required to have more than one part,
         *                               false if it can also just have single part (e.g. foo@com); true by default
         * @return this builder
         */
        public Builder emailDomainMustHaveDot(boolean emailDomainMustHaveDot) {
            this.emailDomainMustHaveDot = emailDomainMustHaveDot;
            return this;
        }

        /**
         * @return the configured link extractor
         */
        public LinkExtractor build() {
            UrlScanner urlScanner = linkTypes.contains(LinkType.URL) ? new UrlScanner() : null;
            WwwScanner wwwScanner = linkTypes.contains(LinkType.WWW) ? new WwwScanner() : null;
            EmailScanner emailScanner = linkTypes.contains(LinkType.EMAIL) ? new EmailScanner(emailDomainMustHaveDot) : null;
            HashtagScanner hashtagScanner = linkTypes.contains(LinkType.HASHTAG) ? new HashtagScanner
                    (allowedHashtagSpecialChars) : null;
            return new LinkExtractor(urlScanner, wwwScanner, emailScanner, hashtagScanner);
        }

        /**
         * Configure {@link HashtagScanner} with the given Set of allowed special characters.
         * Note that the set cannot contain ' ' or '\t' or '\r' or '\n' as they are delimiters of a hashtag.
         *
         * @param allowedSpecialChars set of allowed special characters
         * @return the configured link extractor
         */
        public Builder allowedHashtagSpecialChars(Set<Character> allowedSpecialChars) {
            this.allowedHashtagSpecialChars = allowedSpecialChars;
            return this;
        }
    }

    private class LinkIterator implements Iterator<LinkSpan> {

        private final CharSequence input;

        private LinkSpan next = null;
        private int index = 0;
        private int rewindIndex = 0;

        public LinkIterator(CharSequence input) {
            this.input = input;
        }

        @Override
        public boolean hasNext() {
            setNext();
            return next != null;
        }

        @Override
        public LinkSpan next() {
            if (hasNext()) {
                LinkSpan link = next;
                next = null;
                return link;
            } else {
                throw new NoSuchElementException();
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove");
        }

        private void setNext() {
            if (next != null) {
                return;
            }

            int length = input.length();
            while (index < length) {
                Scanner scanner = trigger(input.charAt(index));
                if (scanner != null) {
                    LinkSpan link = scanner.scan(input, index, rewindIndex);
                    if (link != null) {
                        next = link;
                        index = link.getEndIndex();
                        rewindIndex = index;
                        break;
                    } else {
                        index++;
                    }
                } else {
                    index++;
                }
            }
        }
    }
}
