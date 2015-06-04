autolink-java
=============

Java library to extract links such as URLs and email addresses from plain text.
Fast, small and tries to be smart with matching (text is hard).

Inspired by [Rinku](https://github.com/vmg/rinku). Similar to it, regular
expressions are not used. Instead, the input text is parsed in one pass with
limited backtracking.

Features
--------

### URL extraction

Extracts URLs of the form `scheme://example` with any scheme. URIs such
as `example:test` are not matched (may be added as an option in the future).
If only certain schemes should be allowed, the result can be filtered.

Includes heuristics for not including trailing delimiters such as punctuation
and unbalanced parentheses, see examples below.

Supports internationalized domain names (IDN). Note that they are not validated
and as a result, invalid URLs may be matched.

Example input and the extracted link:

* `http://example.com.` → `http://example.com`
* `http://example.com,` → `http://example.com`
* `(http://example.com)` → `http://example.com`
* `(... (see http://example.com))` → `http://example.com`
* `https://en.wikipedia.org/wiki/Link_(The_Legend_of_Zelda)` →
  `https://en.wikipedia.org/wiki/Link_(The_Legend_of_Zelda)`
* `http://üñîçøðé.com/` → `http://üñîçøðé.com/`

### Email address extraction

Extracts emails such as `foo@example.com`. Doesn't support quoted local parts
such as `"this is sparta"@example.com`. Matches international email addresses,
but doesn't verify the domain name (may match too much).

Examples:

* `foo@example.com` → `foo@example.com`
* `foo@example.com.` → `foo@example.com`
* `foo@example.com,` → `foo@example.com`
* `üñîçøðé@üñîçøðé.com` → `üñîçøðé@üñîçøðé.com`

Usage
-----

Extract links:

```java
import org.nibor.autolink.*;

String input = "wow, so example: http://test.com";
LinkExtractor linkExtractor = LinkExtractor.builder().build();
List<Link> links = linkExtractor.getLinks(input);
Link link = links.get(0);
link.getType();        // LinkType.URL
link.getBeginIndex();  // 17
link.getEndIndex();    // 32
input.substring(link.getBeginIndex(), link.getEndIndex());  // "http://test.com"
```

Wrapping URLs in an <a> tag (doesn't handle escaping, uses Java 8):

```java
import org.nibor.autolink.*;

String input = "wow http://test.com such linked";
LinkExtractor linkExtractor = LinkExtractor.builder()
        .linkTypes(EnumSet.of(LinkType.URL)) // limit to URLs
        .build();
String result = Autolink.renderLinks(input, linkExtractor, (link, sb) -> {
    sb.append("<a href=\"");
    sb.append(input, link.getBeginIndex(), link.getEndIndex());
    sb.append("\">");
    sb.append(input, link.getBeginIndex(), link.getEndIndex());
    sb.append("</a>");
});
result;  // wow <a href="http://test.com">http://test.com</a> such linked
```

Contributing
------------

Pull requests welcome :)!

License
-------

Copyright (c) 2015 Robin Stocker

MIT licensed, see LICENSE file.
