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
public class AutolinkEmailTest extends AutolinkTestCase {

    public static Stream<Arguments> data() {
        return Stream.of(
                arguments(EnumSet.of(LinkType.EMAIL), true),
                arguments(EnumSet.allOf(LinkType.class), true),
                arguments(EnumSet.allOf(LinkType.class), false)
        );
    }

    @Parameter(0)
    public Set<LinkType> linkTypes;

    @Parameter(1)
    public boolean domainMustHaveDot;

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
        assertLinked("foo a@b.com", "foo |a@b.com|");
        assertLinked("a@b.com foo", "|a@b.com| foo");
        assertLinked("\na@b.com", "\n|a@b.com|");
        assertLinked("a@b.com\n", "|a@b.com|\n");
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
    }

    @Test
    public void domainWithoutDot() {
        if (domainMustHaveDot) {
            assertNotLinked("a@b");
            assertNotLinked("a@b.");
            assertLinked("a@b.com.", "|a@b.com|.");
        } else {
            assertLinked("a@b", "|a@b|");
            assertLinked("a@b.", "|a@b|.");
        }
    }

    @Test
    public void dashes() {
        assertLinked("a@example.com-", "|a@example.com|-");
        assertLinked("a@foo-bar.com", "|a@foo-bar.com|");
        assertNotLinked("a@-foo.com");
        if (domainMustHaveDot) {
            assertNotLinked("a@b-.");
        } else {
            assertLinked("a@b-.", "|a@b|-.");
        }
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

    @Test
    public void triggerOverlap() {
        // 'w' is a trigger character for WWW links. Make sure we can rewind enough.
        assertLinked("www@example.com", "|www@example.com|");
    }

    @Test
    public void replyLevel() {
        assertLinked(">foo@example.com", ">|foo@example.com|");
        assertLinked("> foo@example.com", "> |foo@example.com|");
        assertLinked(">>foo@example.com", ">>|foo@example.com|");
        assertLinked(">> foo@example.com", ">> |foo@example.com|");
        assertLinked("> > foo@example.com", "> > |foo@example.com|");
        assertLinked(">>>foo@example.com", ">>>|foo@example.com|");
        assertLinked(">>> foo@example.com", ">>> |foo@example.com|");
        assertLinked("> > > foo@example.com", "> > > |foo@example.com|");
    }

    @Override
    protected LinkExtractor getLinkExtractor() {
        return LinkExtractor.builder().linkTypes(linkTypes).emailDomainMustHaveDot(domainMustHaveDot).build();
    }

    private void assertLinked(String input, String expected) {
        super.assertLinked(input, expected, LinkType.EMAIL);
    }

}
