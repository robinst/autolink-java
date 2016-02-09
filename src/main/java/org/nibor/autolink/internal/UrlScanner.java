package org.nibor.autolink.internal;

import org.nibor.autolink.LinkSpan;
import org.nibor.autolink.LinkType;

/**
 * Scan for URLs starting from the trigger character ":", requires "://".
 * <p>
 * Based on RFC 3986.
 */
public class UrlScanner implements Scanner {

    @Override
    public LinkSpan scan(CharSequence input, int triggerIndex, int rewindIndex) {
        int length = input.length();
        int afterSlashSlash = triggerIndex + 3;
        if (afterSlashSlash >= length || input.charAt(triggerIndex + 1) != '/' || input.charAt(triggerIndex + 2) != '/') {
            return null;
        }

        int first = findFirst(input, triggerIndex - 1, rewindIndex);
        if (first == -1) {
            return null;
        }

        int last = findLast(input, afterSlashSlash);

        return new LinkSpanImpl(LinkType.URL, first, last + 1);
    }

    // See "scheme" in RFC 3986
    private int findFirst(CharSequence input, int beginIndex, int rewindIndex) {
        int first = -1;
        int digit = -1;
        for (int i = beginIndex; i >= rewindIndex; i--) {
            char c = input.charAt(i);
            if (Scanners.isAlpha(c)) {
                first = i;
            } else if (Scanners.isDigit(c)) {
                digit = i;
            } else if (!schemeSpecial(c)) {
                break;
            }
        }
        if (first > 0 && first - 1 == digit) {
            // We don't want to extract "abc://foo" out of "1abc://foo".
            // ".abc://foo" and others are ok though, as they feel more like separators.
            first = -1;
        }
        return first;
    }

    private int findLast(CharSequence input, int beginIndex) {
        int round = 0;
        int square = 0;
        int curly = 0;
        int angle = 0;
        boolean doubleQuote = false;
        boolean singleQuote = false;
        int last = beginIndex;
        loop:
        for (int i = beginIndex; i < input.length(); i++) {
            char c = input.charAt(i);
            switch (c) {
                case ' ':
                case '\t':
                case '\n':
                case '\u000B':
                case '\f':
                case '\r':
                    // These can never be part of an URL, so stop now
                    break loop;
                case '?':
                case '!':
                case '.':
                case ',':
                case ':':
                case ';':
                    // These may be part of an URL but not at the end
                    break;
                case '/':
                    // This may be part of an URL and at the end, but not if the previous character can't be the end of an URL
                    if (last == i - 1) {
                        last = i;
                    }
                    break;
                case '(':
                    round++;
                    break;
                case ')':
                    round--;
                    if (round >= 0) {
                        last = i;
                    } else {
                        // More closing than opening brackets, stop now
                        break loop;
                    }
                    break;
                case '[':
                    square++;
                    break;
                case ']':
                    square--;
                    if (square >= 0) {
                        last = i;
                    } else {
                        // More closing than opening brackets, stop now
                        break loop;
                    }
                    break;
                case '{':
                    curly++;
                    break;
                case '}':
                    curly--;
                    if (curly >= 0) {
                        last = i;
                    } else {
                        // More closing than opening brackets, stop now
                        break loop;
                    }
                    break;
                case '<':
                    angle++;
                    break;
                case '>':
                    angle--;
                    if (angle >= 0) {
                        last = i;
                    } else {
                        // More closing than opening brackets, stop now
                        break loop;
                    }
                    break;
                case '"':
                    doubleQuote = !doubleQuote;
                    if (!doubleQuote) {
                        last = i;
                    }
                    break;
                case '\'':
                    singleQuote = !singleQuote;
                    if (!singleQuote) {
                        last = i;
                    }
                    break;
                default:
                    last = i;
            }
        }
        return last;
    }

    private static boolean schemeSpecial(char c) {
        switch (c) {
            case '+':
            case '-':
            case '.':
                return true;
        }
        return false;
    }
}
