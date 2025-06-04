package org.nibor.autolink;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.Parameter;
import org.junit.jupiter.params.ParameterizedClass;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;

@ParameterizedClass
@MethodSource("data")
public class AutolinkWwwTest extends AutolinkTestCase {

    public static Stream<Arguments> data() {
        return Stream.of(
                arguments(EnumSet.of(LinkType.WWW)),
                arguments(EnumSet.allOf(LinkType.class))
        );
    }

    @Parameter
    public Set<LinkType> linkTypes;

    @Test
    public void notLinked() {
        assertNotLinked("");
        assertNotLinked("wwwsomething.com");
        assertNotLinked("ww.foo.com");
        assertNotLinked("w.bar.foo.co");
        assertNotLinked("www.something");
        assertNotLinked("www.go");
        assertNotLinked("foo.www.fo.uk");
        assertNotLinked("www..com");
        assertNotLinked("wwww.toomany.com");
    }

    @Test
    public void linked() {
        assertLinked("www.s.com", "|www.s.com|");
        assertLinked("www.fo.uk", "|www.fo.uk|");
        assertLinked("foo:www.fo.uk", "foo:|www.fo.uk|");
        assertLinked("foo-www.fo.uk", "foo-|www.fo.uk|");
    }

    @Test
    public void html() {
        assertLinked("<a href=\"somelink\">www.example.org</a>", "<a href=\"somelink\">|www.example.org|</a>");
        assertLinked("<a href=\"www.example.org\">sometext</a>", "<a href=\"|www.example.org|\">sometext</a>");
        assertLinked("<p>www.example.org</p>", "<p>|www.example.org|</p>");
    }

    @Test
    public void multiple() {
        assertLinked("www.one.org/ www.two.org/", "|www.one.org/| |www.two.org/|");
        assertLinked("www.one.org/ : www.two.org/", "|www.one.org/| : |www.two.org/|");
        assertLinked("(www.one.org/)(www.two.org/)", "(|www.one.org/|)(|www.two.org/|)");
    }

    @Test
    public void international() {
        assertLinked("www.üñîçøðé.com/ä", "|www.üñîçøðé.com/ä|");
        assertLinked("www.example.org/\u00A1", "|www.example.org/\u00A1|");
        assertLinked("www.example.org/\u00A2", "|www.example.org/\u00A2|");
    }

    @Test
    public void replyLevel() {
        assertLinked(">www.example.org/", ">|www.example.org/|");
        assertLinked("> www.example.org/", "> |www.example.org/|");
        assertLinked(">>www.example.org/", ">>|www.example.org/|");
        assertLinked(">> www.example.org/", ">> |www.example.org/|");
        assertLinked("> > www.example.org/", "> > |www.example.org/|");
        assertLinked(">>>www.example.org/", ">>>|www.example.org/|");
        assertLinked(">>> www.example.org/", ">>> |www.example.org/|");
        assertLinked("> > > www.example.org/", "> > > |www.example.org/|");
    }

    @Override
    protected LinkExtractor getLinkExtractor() {
        return LinkExtractor.builder().linkTypes(linkTypes).build();
    }

    private void assertLinked(String input, String expected) {
        super.assertLinked(input, expected, LinkType.WWW);
    }
}
