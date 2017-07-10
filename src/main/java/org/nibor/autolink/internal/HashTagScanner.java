package org.nibor.autolink.internal;

import org.nibor.autolink.LinkSpan;
import org.nibor.autolink.LinkType;

import java.util.HashSet;
import java.util.Set;

/**
 * Scan for HashTags starting from the trigger character "#"
 * <p>
 */
public class HashTagScanner implements Scanner {

    public static final char HASHTAG_DELIMITER = ' ';
    private Set<Character> allowedSpecialChars;

    public HashTagScanner(Set<Character> allowedSpecialChars) {
        if (allowedSpecialChars == null) {
            allowedSpecialChars = getDefaultAllowedSpecialChars();
        }
        if (allowedSpecialChars.contains(HASHTAG_DELIMITER)) {
            throw new IllegalArgumentException("Allowed special characters cannot contain " + HASHTAG_DELIMITER);
        }
        this.allowedSpecialChars = allowedSpecialChars;
    }

    @Override
    public LinkSpan scan(CharSequence input, int triggerIndex, int rewindIndex) {
        int last = findLast(input, triggerIndex);
        // should not be an empty hash tag("#")
        if (last == (triggerIndex + 1)) {
            return null;
        }
        // should not start with a number
        if (Scanners.isDigit(input.charAt(triggerIndex + 1))) {
            return null;
        }
        return new LinkSpanImpl(LinkType.HASHTAG, triggerIndex, last);
    }

    private int findLast(CharSequence input, int beginIndex) {
        int i = beginIndex + 1; // skip trigger char
        while (i < input.length()) {
            char c = input.charAt(i);
            if (isHashTagDelimiter(c) || !charAllowed(c)) {
                return i;
            }
            i++;
        }
        return i;
    }

    private boolean isHashTagDelimiter(char c) {
        return (c == HASHTAG_DELIMITER);
    }

    private boolean charAllowed(char c) {
        return Scanners.isAlnum(c) || Scanners.isNonAscii(c) || allowedSpecialChars.contains(c);
    }

    private Set<Character> getDefaultAllowedSpecialChars() {
        Set<Character> chars = new HashSet<>();
        chars.add('_');
        return chars;
    }
}
