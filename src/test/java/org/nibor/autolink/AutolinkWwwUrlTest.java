package org.nibor.autolink;

import java.util.Arrays;
import java.util.EnumSet;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class AutolinkWwwUrlTest extends AutolinkTestCase {

    @Parameters(name = "{1}")
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {LinkExtractor.builder().linkTypes(EnumSet.of(LinkType.WWW)).build(), "WWW"},
                {LinkExtractor.builder().build(), "all"}
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
        assertLinked("www.s.com","|www.s.com|");
        assertLinked("www.fo.uk","|www.fo.uk|");
        assertLinked("foo:www.fo.uk","foo:|www.fo.uk|");
        assertLinked("foo-www.fo.uk","foo-|www.fo.uk|");
        assertLinked("WWW.s.com","|WWW.s.com|");
        assertLinked("Www.s.com","|Www.s.com|");
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

    @Override
    protected LinkExtractor getLinkExtractor() {
        return linkExtractor;
    }

    private void assertLinked(String input, String expected) {
        super.assertLinked(input, expected, LinkType.WWW);
    }
}
