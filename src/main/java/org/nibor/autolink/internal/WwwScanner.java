package org.nibor.autolink.internal;

import org.nibor.autolink.LinkSpan;
import org.nibor.autolink.LinkType;

/**
 * Scan for WWW addresses such as "www.example.org" starting from the trigger character "w".
 * Requires "www." at the beginning and an additional dot in the domain.
 * <p>
 * Based on RFC 3986.
 */
public class WwwScanner implements Scanner {

    @Override
    public LinkSpan scan(final CharSequence input, int triggerIndex, int rewindIndex) {
        final int afterDot = triggerIndex + 4;
        if (afterDot >= input.length() || !isWww(input, triggerIndex)) {
            return null;
        }

        final int first = findFirst(input, triggerIndex, rewindIndex);
        if (first == -1) {
            return null;
        }

        int last = findLast(input, afterDot);
        if (last == -1) {
            return null;
        }

        return new LinkSpanImpl(LinkType.WWW, first, last + 1);
    }

    private static int findFirst(final CharSequence input, final int beginIndex, final int rewindIndex) {
        if (beginIndex == rewindIndex) {
            return beginIndex;
        }

        // Is the character before www. allowed?
        if (isAllowed(input.charAt(beginIndex - 1))) {
            return beginIndex;
        }

        return -1;
    }

    private static int findLast(final CharSequence input, final int beginIndex) {
        final int last = Scanners.findUrlEnd(input, beginIndex);
        if (last == -1) {
            return -1;
        }

        // Make sure there is at least one dot after the first dot,
        // so www.something is not allowed, but www.something.co.uk is
        int pointer = last;
        while (--pointer > beginIndex) {
            if (input.charAt(pointer) == '.' && pointer > beginIndex) {
                return last;
            }
        }

        return -1;
    }

    private static boolean isAllowed(char c) {
        return c != '.' && !Scanners.isAlnum(c);
    }

    private static boolean isWww(final CharSequence input, final int triggerIndex) {
        return input.charAt(triggerIndex + 1) == 'w'
                && input.charAt(triggerIndex + 2) == 'w'
                && input.charAt(triggerIndex + 3) == '.';
    }
}
