package org.nibor.autolink;

import java.util.Arrays;
import java.util.EnumSet;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class AutolinkWwwUrlTest extends AutolinkUrlTest {

    @Parameters(name = "{1}")
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {LinkExtractor.builder().linkTypes(EnumSet.of(LinkType.URL)).build(), "URL"},
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
    }
    
    @Test
    public void linked() {
        assertLinked("www.s.com","|www.s.com|");
        assertLinked("www.fo.uk","|www.fo.uk|");
    }

    @Test
    public void schemes() {
        assertLinked("http://www.something.com","|http://www.something.com|");
        assertLinked("http://something.com","|http://something.com|");
        assertLinked("http://something.co.uk","|http://something.co.uk|");
    }

    @Override
    protected LinkExtractor getLinkExtractor() {
        return linkExtractor;
    }

    private void assertLinked(String input, String expected) {
        super.assertLinked(input, expected, LinkType.URL);
    }
}
