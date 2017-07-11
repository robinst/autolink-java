package org.nibor.autolink.internal;

import org.nibor.autolink.LinkSpan;
import org.nibor.autolink.LinkType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Scan for HashTags starting from the trigger character "#"
 * <p>
 */
public class HashtagScanner implements Scanner {

    public static final Set<Character> HASHTAG_DELIMITERS = new HashSet<>(Arrays.asList(' ', '\t', '\n', '\r'));
    private Set<Character> allowedSpecialChars;

    public HashtagScanner(Set<Character> allowedSpecialChars) {
        if (allowedSpecialChars == null) {
            allowedSpecialChars = getDefaultAllowedSpecialChars();
        }
        if (isValidAllowedSpecialCharsSet(allowedSpecialChars)) {
            throw new IllegalArgumentException("Allowed special characters cannot contain ' ', '\\t', '\\r', '\\n'");
        }
        this.allowedSpecialChars = allowedSpecialChars;
    }

    @Override
    public LinkSpan scan(CharSequence input, int triggerIndex, int rewindIndex) {
        if (!startsWithHash(input, triggerIndex, rewindIndex)) {
            return null;
        }
        int last = findLast(input, triggerIndex);
        // should not be an empty hashtag("#")
        if (last == (triggerIndex + 1)) {
            return null;
        }
        // should not be all numeric
        if (isNumeric(input, triggerIndex, last)) {
            return null;
        }
        return new LinkSpanImpl(LinkType.HASHTAG, triggerIndex, last);
    }

    private int findLast(CharSequence input, int beginIndex) {
        int i = beginIndex + 1; // skip trigger char
        while (i < input.length()) {
            char c = input.charAt(i);
            if (isHashtagDelimiter(c) || !charAllowed(c)) {
                return i;
            }
            i++;
        }
        return i;
    }

    private boolean charAllowed(char c) {
        return Scanners.isAlnum(c) || Scanners.isNonAscii(c) || allowedSpecialChars.contains(c);
    }

    private static boolean startsWithHash(CharSequence input, int triggerIndex, int rewindIndex) {
        if (triggerIndex == 0) {
            return true;
        }
        char precedingChar = input.charAt(triggerIndex - 1);
        return !(Scanners.isAlnum(precedingChar) || Scanners.isNonAscii(precedingChar));
    }

    private static boolean isValidAllowedSpecialCharsSet(Set<Character> allowedSpecialChars) {
        Set<Character> intersection = new HashSet<>(allowedSpecialChars);
        intersection.retainAll(HASHTAG_DELIMITERS);
        return !intersection.isEmpty();
    }

    private static boolean isHashtagDelimiter(char c) {
        return HASHTAG_DELIMITERS.contains(c);
    }

    private Set<Character> getDefaultAllowedSpecialChars() {
        Set<Character> chars = new HashSet<>();
        chars.add('_');
        return chars;
    }

    private static boolean isNumeric(CharSequence input, int beginIndex, int endIndex) {
        for (int i = beginIndex + 1; i < endIndex; i++) {
            if (!Character.isDigit(input.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}

