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

        int last = Scanners.findUrlEnd(input, afterSlashSlash);
        if (last == -1) {
            return null;
        }

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
