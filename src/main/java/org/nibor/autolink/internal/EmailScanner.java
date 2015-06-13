package org.nibor.autolink.internal;

import org.nibor.autolink.LinkSpan;
import org.nibor.autolink.LinkType;

/**
 * Scan for email address starting from the trigger character "@".
 * <p/>
 * Based on RFC 6531, but also scans invalid IDN. Doesn't match IP address in domain part or quoting in local part.
 */
public class EmailScanner implements Scanner {

    private final boolean domainMustHaveDot;

    public EmailScanner(boolean domainMustHaveDot) {
        this.domainMustHaveDot = domainMustHaveDot;
    }

    @Override
    public LinkSpan scan(CharSequence input, int triggerIndex, int rewindIndex) {
        int beforeAt = triggerIndex - 1;
        int first = findFirst(input, beforeAt, rewindIndex);
        if (first == -1) {
            return null;
        }

        int afterAt = triggerIndex + 1;
        int last = findLast(input, afterAt);
        if (last == -1) {
            return null;
        }

        return new LinkSpanImpl(LinkType.EMAIL, first, last + 1);
    }

    // See "Local-part" in RFC 5321, plus extensions in RFC 6531
    private int findFirst(CharSequence input, int beginIndex, int rewindIndex) {
        int first = -1;
        boolean atomBoundary = true;
        for (int i = beginIndex; i >= rewindIndex; i--) {
            char c = input.charAt(i);
            if (localAtomAllowed(c)) {
                first = i;
                atomBoundary = false;
            } else if (c == '.') {
                if (atomBoundary) {
                    break;
                }
                atomBoundary = true;
            } else {
                break;
            }
        }
        return first;
    }

    // See "Domain" in RFC 5321, plus extension of "sub-domain" in RFC 6531
    private int findLast(CharSequence input, int beginIndex) {
        boolean firstInSubDomain = true;
        boolean canEndSubDomain = false;
        int firstDot = -1;
        int last = -1;
        for (int i = beginIndex; i < input.length(); i++) {
            char c = input.charAt(i);
            if (firstInSubDomain) {
                if (subDomainAllowed(c)) {
                    last = i;
                    firstInSubDomain = false;
                    canEndSubDomain = true;
                } else {
                    break;
                }
            } else {
                if (c == '.') {
                    if (!canEndSubDomain) {
                        break;
                    }
                    firstInSubDomain = true;
                    if (firstDot == -1) {
                        firstDot = i;
                    }
                } else if (c == '-') {
                    canEndSubDomain = false;
                } else if (subDomainAllowed(c)) {
                    last = i;
                    canEndSubDomain = true;
                } else {
                    break;
                }
            }
        }
        if (domainMustHaveDot && (firstDot == -1 || firstDot > last)) {
            return -1;
        } else {
            return last;
        }
    }

    // See "Atom" in RFC 5321, "atext" in RFC 5322
    private boolean localAtomAllowed(char c) {
        if (Scanners.isAlnum(c) || Scanners.isNonAscii(c)) {
            return true;
        }
        switch (c) {
            case '!':
            case '#':
            case '$':
            case '%':
            case '&':
            case '\'':
            case '*':
            case '+':
            case '-':
            case '/':
            case '=':
            case '?':
            case '^':
            case '_':
            case '`':
            case '{':
            case '|':
            case '}':
            case '~':
                return true;
        }
        return false;
    }

    // See "sub-domain" in RFC 5321. Extension in RFC 6531 is simplified, this can also match invalid domains.
    private boolean subDomainAllowed(char c) {
        return Scanners.isAlnum(c) || Scanners.isNonAscii(c);
    }

}
