package org.nibor.autolink;

/**
 * Type of extracted link.
 */
public enum LinkType {
    /**
     * URL such as {@code http://example.com}
     */
    URL,
    /**
     * Email address such as {@code foo@example.com}
     */
    EMAIL,
    /**
     * URL such as {@code www.example.com}
     */
    WWW
}
