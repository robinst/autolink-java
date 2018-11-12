package org.nibor.autolink.internal;

public class Scanners {

    public static boolean isAlpha(char c) {
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
    }

    public static boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    public static boolean isAlnum(char c) {
        return isAlpha(c) || isDigit(c);
    }

    public static boolean isNonAscii(char c) {
        return c >= 0x80;
    }

    public static int findUrlEnd(CharSequence input, int beginIndex) {
        int round = 0;
        int square = 0;
        int curly = 0;
        boolean doubleQuote = false;
        boolean singleQuote = false;
        int last = -1;
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
                // These are part of "fragment percent-encode set" which means they need to be
                // percent-encoded in an URL: https://url.spec.whatwg.org/#fragment-percent-encode-set
                case ' ':
                case '\"':
                case '<':
                case '>':
                case '`':
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
                    // The above can never be part of an URL, so stop now. See RFC 3986 and RFC 3987.
                    // Some characters are not in the above list, even they are not in "unreserved" or "reserved":
                    //   '\\', '^', '{', '|', '}'
                    // The reason for this is that other link detectors also allow them. Also see below, we require
                    // the braces to be balanced.
                case '\u00A0': // no-break space
                case '\u2000': // en quad
                case '\u2001': // em quad
                case '\u2002': // en space
                case '\u2003': // em space
                case '\u2004': // three-per-em space
                case '\u2005': // four-per-em space
                case '\u2006': // six-per-em space
                case '\u2007': // figure space
                case '\u2008': // punctuation space
                case '\u2009': // thin space
                case '\u200A': // hair space
                case '\u2028': // line separator
                case '\u2029': // paragraph separator
                case '\u202F': // narrow no-break space
                case '\u205F': // medium mathematical space
                case '\u3000': // ideographic space
                    // While these are allowed by RFC 3987, they are Unicode whitespace characters
                    // that look like a space, so it would be confusing not to end URLs.
                    // They are also excluded from IDNs by some browsers.
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
}
