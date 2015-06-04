package org.nibor.autolink.internal;

import org.nibor.autolink.LinkType;

public interface Scanner {

    /**
     * @return the type of link that this scanner tries to match
     */
    LinkType getLinkType();

    /**
     * @param input input text
     * @param triggerIndex the index at which the trigger character for this scanner was
     * @param rewindIndex the index that can maximally be rewound to (either the very first character of the input or
     * the character after the last matched link)
     * @param result when a match was successful (true is returned), the beginIndex (inclusive) and endIndex (exclusive)
     * need to be set to be set here
     * @return true if matched, false otherwise
     */
    boolean scan(CharSequence input, int triggerIndex, int rewindIndex, int[] result);

}
