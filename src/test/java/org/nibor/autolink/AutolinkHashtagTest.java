package org.nibor.autolink;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;

@RunWith(Parameterized.class)
public class AutolinkHashtagTest extends AutolinkTestCase {
    @Parameters(name = "{1}")
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {LinkExtractor.builder().linkTypes(EnumSet.of(LinkType.HASHTAG)).build(), "HASHTAG"},
                {LinkExtractor.builder().linkTypes(EnumSet.allOf(LinkType.class)).build(), "all"},
                {LinkExtractor.builder()
                        .allowedHashtagSpecialChars(new HashSet<Character>() {
                            {add('-');
                                add('_');}
                        })
                        .linkTypes(EnumSet.of(LinkType.HASHTAG))
                        .build(), "Allow -"}
        });
    }

    @Parameter(0)
    public LinkExtractor linkExtractor;

    @Parameter(1)
    public String description;

    @Test
    public void notLinked() {
        assertNotLinked("");
        assertNotLinked(" ");
        assertNotLinked("foo");
        assertNotLinked("#");
        assertNotLinked("###");
        assertNotLinked("#1");
        assertNotLinked("#100");
        assertNotLinked("foo#bar");
    }

    @Test
    public void linking() {
        assertLinked("#longdrive", "|#longdrive|");
        assertLinked("#longdrive.", "|#longdrive|.");
        assertLinked("#long_drive", "|#long_drive|");
        assertLinked("5 hours of #longdrive was good", "5 hours of |#longdrive| was good");
        assertLinked("5 hours of #longdrive. 5 hours of #trek", "5 hours of |#longdrive|. 5 hours of |#trek|");
        assertLinked("#long long-drive", "|#long| long-drive");
        assertLinked("#longdrive123", "|#longdrive123|");

        if (description.equals("Allow -")) {
            assertLinked("#long-drive", "|#long-drive|");
            assertLinked("#long #long-drive", "|#long| |#long-drive|");
        } else {
            assertLinked("#long-drive", "|#long|-drive");
            assertLinked("#long #long-drive", "|#long| |#long|-drive");
        }
    }

    @Test
    public void alphaAndDigit() {
        assertNotLinked("5#longdrive");
        assertLinked("#longdrive5", "|#longdrive5|");
        assertLinked("#5longdrive", "|#5longdrive|");
    }

    @Test
    public void beforeAndAfterBracket() {
        assertLinked("[#longdrive]", "[|#longdrive|]");
        assertLinked("]#longdrive]", "]|#longdrive|]");
        assertLinked("`#longdrive`", "`|#longdrive|`");
        assertLinked("[#longdrive][#trek]", "[|#longdrive|][|#trek|]");
    }

    @Test
    public void beforeAndAfterSpace() {
        assertLinked(" #longdrive ", " |#longdrive| ");
        assertLinked("\n#longdrive\n", "\n|#longdrive|\n");
        assertLinked("\t#longdrive\t", "\t|#longdrive|\t");
        assertLinked("\r#longdrive\r", "\r|#longdrive|\r");
    }

    @Test
    public void international() {
        assertLinked("#王岐山", "|#王岐山|");
        assertLinked("王岐山 #王岐山 王岐山", "王岐山 |#王岐山| 王岐山");
        assertLinked("#üñîçøðé@üñîçøðé", "|#üñîçøðé|@üñîçøðé");
        assertLinked("#üñîçøðéabcdef123", "|#üñîçøðéabcdef123|");
    }

    @Test
    public void replyLevel() {
        assertLinked(">#longdrive", ">|#longdrive|");
        assertLinked("> #longdrive", "> |#longdrive|");
        assertLinked(">>#longdrive", ">>|#longdrive|");
        assertLinked(">> #longdrive", ">> |#longdrive|");
        assertLinked("> > #longdrive", "> > |#longdrive|");
        assertLinked(">>>#longdrive", ">>>|#longdrive|");
        assertLinked(">>> #longdrive", ">>> |#longdrive|");
        assertLinked("> > > #longdrive", "> > > |#longdrive|");
    }

    @Test
    public void mixed() {
        if (description.equals("all")) {
            assertLinked("https://www.slf4j.org/codes.html#multiple_bindings",
                    "|https://www.slf4j.org/codes.html#multiple_bindings|", LinkType.URL);
        } else {
            assertNotLinked("https://www.slf4j.org/codes.html#multiple_bindings");
        }
    }

    @Override
    protected LinkExtractor getLinkExtractor() {
        return linkExtractor;
    }

    protected void assertLinked(String input, String expected) {
        super.assertLinked(input, expected, LinkType.HASHTAG);
    }

    protected void assertLinked(String input, String expected, LinkType type) {
        super.assertLinked(input, expected, type);
    }
}
