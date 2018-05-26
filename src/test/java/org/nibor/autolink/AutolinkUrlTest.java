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
        assertNotLinked("1abc://foo");
        assertLinked("a://foo", "|a://foo|");
        assertLinked("a123://foo", "|a123://foo|");
        assertLinked("a123b://foo", "|a123b://foo|");
        assertLinked("a+b://foo", "|a+b://foo|");
        assertLinked("a-b://foo", "|a-b://foo|");
        assertLinked("a.b://foo", "|a.b://foo|");
        assertLinked("ABC://foo", "|ABC://foo|");
    }

    @Test
    public void authority() {
        assertLinked("ab://", "ab://");
        assertLinked("http://", "http://");
        assertLinked("http:// ", "http:// ");
        assertLinked("\"http://\"", "\"http://\"");
        assertLinked("\"http://...\", ", "\"http://...\", ");

        assertLinked("http://a.", "|http://a|.");
    }

    @Test
    public void linking() {
        assertLinked("ab://c", "|ab://c|");
        assertLinked("http://example.org/", "|http://example.org/|");
        assertLinked("http://example.org/123", "|http://example.org/123|");
        assertLinked("http://example.org/?foo=test&bar=123", "|http://example.org/?foo=test&bar=123|");
        assertLinked("http://example.org/?foo=%20", "|http://example.org/?foo=%20|");
        assertLinked("http://example.org/%3C", "|http://example.org/%3C|");
    }

    @Test
    public void schemeSeparatedByNonAlphanumeric() {
        assertLinked(".http://example.org/", ".|http://example.org/|");
    }

    @Test
    public void spaceCharactersStopUrl() {
        assertLinked("foo http://example.org/", "foo |http://example.org/|");
        assertLinked("http://example.org/ bar", "|http://example.org/| bar");
        assertLinked("http://example.org/\tbar", "|http://example.org/|\tbar");
        assertLinked("http://example.org/\nbar", "|http://example.org/|\nbar");
        assertLinked("http://example.org/\u000Bbar", "|http://example.org/|\u000Bbar");
        assertLinked("http://example.org/\fbar", "|http://example.org/|\fbar");
        assertLinked("http://example.org/\rbar", "|http://example.org/|\rbar");
    }

    @Test
    public void illegalCharactersStopUrl() {
        assertLinked("http://example.org/<", "|http://example.org/|<");
        assertLinked("http://example.org/>", "|http://example.org/|>");
        assertLinked("http://example.org/<>", "|http://example.org/|<>");
        assertLinked("http://example.org/\u0000", "|http://example.org/|\u0000");
        assertLinked("http://example.org/\u000E", "|http://example.org/|\u000E");
        assertLinked("http://example.org/\u007F", "|http://example.org/|\u007F");
        assertLinked("http://example.org/\u009F", "|http://example.org/|\u009F");
    }

    @Test
    public void delimiterAtEnd() {
        assertLinked("http://example.org/.", "|http://example.org/|.");
        assertLinked("http://example.org/..", "|http://example.org/|..");
        assertLinked("http://example.org/,", "|http://example.org/|,");
        assertLinked("http://example.org/:", "|http://example.org/|:");
        assertLinked("http://example.org/?", "|http://example.org/|?");
        assertLinked("http://example.org/!", "|http://example.org/|!");
        assertLinked("http://example.org/;", "|http://example.org/|;");
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
        assertLinked("http://foo[.]example[.]org/abc/", "|http://foo[.]example[.]org/abc/|");
    }

    @Test
    public void matchingPunctuationTricky() {
        assertLinked("((http://example.org/))", "((|http://example.org/|))");
        assertLinked("((http://example.org/a(b)))", "((|http://example.org/a(b)|))");
        assertLinked("[(http://example.org/)]", "[(|http://example.org/|)]");
        assertLinked("(http://example.org/).", "(|http://example.org/|).");
        assertLinked("(http://example.org/.)", "(|http://example.org/|.)");
        assertLinked("http://example.org/>", "|http://example.org/|>");
        // not sure about these
        assertLinked("http://example.org/(", "|http://example.org/|(");
        assertLinked("http://example.org/(.", "|http://example.org/|(.");
        assertLinked("http://example.org/]()", "|http://example.org/|]()");
    }

    @Test
    public void quotes() {
        assertLinked("http://example.org/\"_(foo)", "|http://example.org/\"_(foo)|");
        assertLinked("http://example.org/\"_(foo)\"", "|http://example.org/\"_(foo)\"|");
        assertLinked("http://example.org/\"\"", "|http://example.org/\"\"|");
        assertLinked("http://example.org/\"\"\"", "|http://example.org/\"\"|\"");
        assertLinked("http://example.org/\".", "|http://example.org/|\".");
        assertLinked("http://example.org/\"a", "|http://example.org/\"a|");
        assertLinked("http://example.org/it's", "|http://example.org/it's|");
    }

    @Test
    public void html() {
        assertLinked("http://example.org\">", "|http://example.org|\">");
        assertLinked("http://example.org'>", "|http://example.org|'>");
        assertLinked("http://example.org\"/>", "|http://example.org|\"/>");
        assertLinked("http://example.org'/>", "|http://example.org|'/>");
        assertLinked("http://example.org<p>", "|http://example.org|<p>");
        assertLinked("http://example.org</p>", "|http://example.org|</p>");
    }

    @Test
    public void css() {
        assertLinked("http://example.org\");", "|http://example.org|\");");
        assertLinked("http://example.org');", "|http://example.org|');");
    }

    @Test
    public void slash() {
        assertLinked("http://example.org/", "|http://example.org/|");
        assertLinked("http://example.org/a/", "|http://example.org/a/|");
        assertLinked("http://example.org//", "|http://example.org//|");
    }

    @Test
    public void multiple() {
        assertLinked("http://one.org/ http://two.org/", "|http://one.org/| |http://two.org/|");
        assertLinked("http://one.org/ : http://two.org/", "|http://one.org/| : |http://two.org/|");
        assertLinked("(http://one.org/)(http://two.org/)", "(|http://one.org/|)(|http://two.org/|)");
    }

    @Test
    public void international() {
        assertLinked("http://üñîçøðé.com/ä", "|http://üñîçøðé.com/ä|");
        assertLinked("http://example.org/\u00A1", "|http://example.org/\u00A1|");
        assertLinked("http://example.org/\u00A2", "|http://example.org/\u00A2|");
    }

    @Test
    public void unicodeWhitespace() {
        char[] whitespace = new char[] {
            '\u00A0', // no-break space
            '\u2000', // en quad
            '\u2001', // em quad
            '\u2002', // en space
            '\u2003', // em space
            '\u2004', // three-per-em space
            '\u2005', // four-per-em space
            '\u2006', // six-per-em space
            '\u2007', // figure space
            '\u2008', // punctuation space
            '\u2009', // thin space
            '\u200A', // hair space
            '\u2028', // line separator
            '\u2029', // paragraph separator
            '\u202F', // narrow no-break space
            '\u205F', // medium mathematical space
            '\u3000', // ideographic space
        };

        for (char c : whitespace) {
            assertLinked("http://example.org" + c, "|http://example.org|" + c);
        }
    }

    @Test
    public void replyLevel() {
        assertLinked(">http://example.org/", ">|http://example.org/|");
        assertLinked("> http://example.org/", "> |http://example.org/|");
        assertLinked(">>http://example.org/", ">>|http://example.org/|");
        assertLinked(">> http://example.org/", ">> |http://example.org/|");
        assertLinked("> > http://example.org/", "> > |http://example.org/|");
        assertLinked(">>>http://example.org/", ">>>|http://example.org/|");
        assertLinked(">>> http://example.org/", ">>> |http://example.org/|");
        assertLinked("> > > http://example.org/", "> > > |http://example.org/|");
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

    protected void assertLinked(String input, String expected) {
        super.assertLinked(input, expected, LinkType.URL);
    }
}
