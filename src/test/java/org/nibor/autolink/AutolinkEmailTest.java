package org.nibor.autolink;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.EnumSet;

@RunWith(Parameterized.class)
public class AutolinkEmailTest extends AutolinkTestCase {

    @Parameters(name = "{1}")
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {LinkExtractor.builder().linkTypes(EnumSet.of(LinkType.EMAIL)).build(), "email"},
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
        assertNotLinked("@");
        assertNotLinked("a@");
        assertNotLinked("@a");
        assertNotLinked("@@@");
    }

    @Test
    public void simple() {
        assertLinked("a@b", "|a@b|");
        assertLinked("foo@example.com", "|foo@example.com|");
        assertLinked("foo.bar@example.com", "|foo.bar@example.com|");
    }

    @Test
    public void allowedText() {
        // I know, I know...
        assertLinked("#!$%&'*+-/=?^_`{}|~@example.org", "|#!$%&'*+-/=?^_`{}|~@example.org|");
    }

    @Test
    public void spaceSeparation() {
        assertLinked("foo a@b", "foo |a@b|");
        assertLinked("a@b foo", "|a@b| foo");
        assertLinked("\na@b", "\n|a@b|");
        assertLinked("a@b\n", "|a@b|\n");
    }

    @Test
    public void specialSeparation() {
        assertLinked("(a@example.com)", "(|a@example.com|)");
        assertLinked("\"a@example.com\"", "\"|a@example.com|\"");
        assertLinked("\"a@example.com\"", "\"|a@example.com|\"");
        assertLinked(",a@example.com,", ",|a@example.com|,");
        assertLinked(":a@example.com:", ":|a@example.com|:");
        assertLinked(";a@example.com;", ";|a@example.com|;");
    }

    @Test
    public void dots() {
        assertNotLinked(".@example.com");
        assertNotLinked("foo.@example.com");
        assertLinked(".foo@example.com", ".|foo@example.com|");
        assertLinked(".foo@example.com", ".|foo@example.com|");
        assertLinked("a..b@example.com", "a..|b@example.com|");
        assertLinked("a@example.com.", "|a@example.com|.");
        assertLinked("a@b..com", "|a@b|..com");
    }

    @Test
    public void dashes() {
        assertLinked("a@example.com-", "|a@example.com|-");
        assertLinked("a@foo-bar.com", "|a@foo-bar.com|");
        assertLinked("a@b-.", "|a@b|-.");
        assertNotLinked("a@-foo.com");
    }

    @Test
    public void multiple() {
        assertLinked("a@example.com b@example.com", "|a@example.com| |b@example.com|");
        assertLinked("a@example.com @ b@example.com", "|a@example.com| @ |b@example.com|");
    }

    @Test
    public void international() {
        assertLinked("üñîçøðé@example.com", "|üñîçøðé@example.com|");
        assertLinked("üñîçøðé@üñîçøðé.com", "|üñîçøðé@üñîçøðé.com|");
    }

    @Override
    protected LinkExtractor getLinkExtractor() {
        return linkExtractor;
    }

    private void assertLinked(String input, String expected) {
        super.assertLinked(input, expected, LinkType.EMAIL);
    }

}
