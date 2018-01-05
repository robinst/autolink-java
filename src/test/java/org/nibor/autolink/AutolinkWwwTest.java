package org.nibor.autolink;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.EnumSet;

@RunWith(Parameterized.class)
public class AutolinkWwwTest extends AutolinkTestCase {

    @Parameters(name = "{1}")
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {LinkExtractor.builder().linkTypes(EnumSet.of(LinkType.WWW)).build(), "WWW"},
                {LinkExtractor.builder().linkTypes(EnumSet.allOf(LinkType.class)).build(), "all"}
        });
    }

    @Parameter(0)
    public LinkExtractor linkExtractor;

    @Parameter(1)
    public String description;

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
        assertNotLinked("www.üñîçøðé.com/ä");
        assertLinked("www.example.org/\u00A1", "|www.example.org/|\u00A1");
        assertLinked("www.example.org/\u00A2", "|www.example.org/|\u00A2");
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
        return linkExtractor;
    }

    private void assertLinked(String input, String expected) {
        super.assertLinked(input, expected, LinkType.WWW);
    }
}
