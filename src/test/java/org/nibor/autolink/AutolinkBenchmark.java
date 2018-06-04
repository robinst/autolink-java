package org.nibor.autolink;

import org.openjdk.jmh.Main;
import org.openjdk.jmh.annotations.Benchmark;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class AutolinkBenchmark extends AutolinkTestCase {

    private static final List<String> WORDS = Arrays.asList(
            "Lorem ", "ipsum ", "dolor ", "sit ", "amet ", "consectetur ", "adipiscing ", "elit ",
            ".", ",", ":", "@", "(", ")", "http://example.com", "https://test.com/foo_(bar)",
            "www.something.com", "www.another.uk.co",
            "foo.bar@example.com", "foo+bar@test.com"
    );

    private static final String GENERATED_INPUT = generateText(WORDS, 10000);

    public static void main(String[] args) throws Exception {
        System.out.println("input length: " + GENERATED_INPUT.length());
        Iterable<LinkSpan> links = LinkExtractor.builder().build().extractLinks(GENERATED_INPUT);
        int count = 0;
        for (LinkSpan ignore : links) {
            count++;
        }
        System.out.println("number of links: " + count);
        System.out.println();
        Main.main(args);
    }

    @Benchmark
    public void generatedText() {
        renderExtractedLinks(GENERATED_INPUT, "|", null);
    }

    @Override
    protected LinkExtractor getLinkExtractor() {
        return LinkExtractor.builder().build();
    }

    private static String generateText(List<String> wordList, int words) {
        Random random = new Random(1);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < words; i++) {
            String word = wordList.get(random.nextInt(wordList.size()));
            sb.append(word);
        }
        return sb.toString();
    }
}
