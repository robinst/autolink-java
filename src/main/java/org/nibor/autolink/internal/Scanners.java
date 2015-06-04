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
}
