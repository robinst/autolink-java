package org.nibor.autolink;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.EnumSet;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class AutolinkUrlTest extends AutolinkTestCase {

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
        assertNotLinked("foo");
        assertNotLinked(":");
        assertNotLinked("://");
        assertNotLinked(":::");
    }

    @Test
    public void schemes() {
        assertNotLinked("://foo");
        assertNotLinked("1://foo");
        assertNotLinked("123://foo");
        assertNotLinked("+://foo");
        assertNotLinked("-://foo");
        assertNotLinked(".://foo");
        assertLinked("a://foo", "|a://foo|");
        assertLinked("a123://foo", "|a123://foo|");
        assertLinked("a+b://foo", "|a+b://foo|");
        assertLinked("a-b://foo", "|a-b://foo|");
        assertLinked("a.b://foo", "|a.b://foo|");
        assertLinked("ABC://foo", "|ABC://foo|");
    }

    @Test
    public void hostTooShort() {
        assertLinked("ab://", "ab://");
    }

    @Test
    public void linking() {
        assertLinked("ab://c", "|ab://c|");
        assertLinked("http://example.org/", "|http://example.org/|");
        assertLinked("http://example.org/123", "|http://example.org/123|");
        assertLinked("http://example.org/?foo=test&bar=123", "|http://example.org/?foo=test&bar=123|");
    }

    @Test
    public void schemeSeparatedByNonAlphanumeric() {
        assertLinked(".http://example.org/", ".|http://example.org/|");
    }

    @Test
    public void spaceSeparation() {
        assertLinked("foo http://example.org/", "foo |http://example.org/|");
        assertLinked("http://example.org/ bar", "|http://example.org/| bar");
    }

    @Test
    public void delimiterSeparation() {
        assertLinked("http://example.org/.", "|http://example.org/|.");
        assertLinked("http://example.org/..", "|http://example.org/|..");
        assertLinked("http://example.org/,", "|http://example.org/|,");
        assertLinked("http://example.org/:", "|http://example.org/|:");
        assertLinked("http://example.org/?", "|http://example.org/|?");
        assertLinked("http://example.org/!", "|http://example.org/|!");
    }

    @Test
    public void matchingPunctuation() {
        assertLinked("http://example.org/a(b)", "|http://example.org/a(b)|");
        assertLinked("http://example.org/a[b]", "|http://example.org/a[b]|");
        assertLinked("http://example.org/a{b}", "|http://example.org/a{b}|");
        assertLinked("http://example.org/a\"b\"", "|http://example.org/a\"b\"|");
        assertLinked("http://example.org/a'b'", "|http://example.org/a'b'|");
        assertLinked("(http://example.org/)", "(|http://example.org/|)");
        assertLinked("[http://example.org/]", "[|http://example.org/|]");
        assertLinked("{http://example.org/}", "{|http://example.org/|}");
        assertLinked("\"http://example.org/\"", "\"|http://example.org/|\"");
        assertLinked("'http://example.org/'", "'|http://example.org/|'");
    }

    @Test
    public void matchingPunctuationTricky() {
        assertLinked("((http://example.org/))", "((|http://example.org/|))");
        assertLinked("((http://example.org/a(b)))", "((|http://example.org/a(b)|))");
        assertLinked("[(http://example.org/)]", "[(|http://example.org/|)]");
        assertLinked("(http://example.org/).", "(|http://example.org/|).");
        assertLinked("(http://example.org/.)", "(|http://example.org/|.)");
        // not sure about these:
        assertLinked("http://example.org/(", "|http://example.org/(|");
        assertLinked("http://example.org/]()", "|http://example.org/|]()");
    }

    @Test
    public void multiple() {
        assertLinked("http://one.org/ http://two.org/", "|http://one.org/| |http://two.org/|");
        assertLinked("http://one.org/ : http://two.org/", "|http://one.org/| : |http://two.org/|");
    }

    @Test
    public void international() {
        assertLinked("http://üñîçøðé.com/ä", "|http://üñîçøðé.com/ä|");
    }

    @Test
    public void linkToString() {
        Iterable<LinkSpan> links = getLinkExtractor().extractLinks("wow, so example: http://test.com");
        assertEquals("Link{type=URL, beginIndex=17, endIndex=32}", links.iterator().next().toString());
    }

    @Override
    protected LinkExtractor getLinkExtractor() {
        return linkExtractor;
    }

    private void assertLinked(String input, String expected) {
        super.assertLinked(input, expected, LinkType.URL);
    }
}
