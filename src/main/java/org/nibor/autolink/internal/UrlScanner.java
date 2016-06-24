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
        boolean doubleQuote = false;
        boolean singleQuote = false;
        int last = beginIndex;
        loop:
        for (int i = beginIndex; i < input.length(); i++) {
            char c = input.charAt(i);
            switch (c) {
                case '\u0000':
                case '\u0001':
                case '\u0002':
                case '\u0003':
                case '\u0004':
                case '\u0005':
                case '\u0006':
                case '\u0007':
                case '\u0008':
                case '\t':
                case '\n':
                case '\u000B':
                case '\f':
                case '\r':
                case '\u000E':
                case '\u000F':
                case '\u0010':
                case '\u0011':
                case '\u0012':
                case '\u0013':
                case '\u0014':
                case '\u0015':
                case '\u0016':
                case '\u0017':
                case '\u0018':
                case '\u0019':
                case '\u001A':
                case '\u001B':
                case '\u001C':
                case '\u001D':
                case '\u001E':
                case '\u001F':
                case ' ':
                case '<':
                case '>':
                case '\u007F':
                case '\u0080':
                case '\u0081':
                case '\u0082':
                case '\u0083':
                case '\u0084':
                case '\u0085':
                case '\u0086':
                case '\u0087':
                case '\u0088':
                case '\u0089':
                case '\u008A':
                case '\u008B':
                case '\u008C':
                case '\u008D':
                case '\u008E':
                case '\u008F':
                case '\u0090':
                case '\u0091':
                case '\u0092':
                case '\u0093':
                case '\u0094':
                case '\u0095':
                case '\u0096':
                case '\u0097':
                case '\u0098':
                case '\u0099':
                case '\u009A':
                case '\u009B':
                case '\u009C':
                case '\u009D':
                case '\u009E':
                case '\u009F':
                    // These can never be part of an URL, so stop now. See RFC 3986 and RFC 3987.
                    // Some characters are not in the above list, even they are not in "unreserved" or "reserved":
                    //   '"', '\\', '^', '`', '{', '|', '}'
                    // The reason for this is that other link detectors also allow them. Also see below, we require
                    // the quote and the braces to be balanced.
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
                    // Allowed in IPv6 address host
                    square++;
                    break;
                case ']':
                    // Allowed in IPv6 address host
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
