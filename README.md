autolink-java
=============

Java library to extract links such as URLs and email addresses from plain text.
Fast, small and tries to be smart with matching (text is hard).

Inspired by [Rinku](https://github.com/vmg/rinku). Similar to it, regular
expressions are not used. Instead, the input text is parsed in one pass with
limited backtracking.

This library requires Java 7. It works on Android (minimum API level 15). It has no external dependencies.

Maven coordinates
(see
[here](https://search.maven.org/#artifactdetails|org.nibor.autolink|autolink|0.5.0|jar)
for other build systems):

```xml
<dependency>
    <groupId>org.nibor.autolink</groupId>
    <artifactId>autolink</artifactId>
    <version>0.5.0</version>
</dependency>
```

[![Build status](https://travis-ci.org/robinst/autolink-java.svg?branch=master)](https://travis-ci.org/robinst/autolink-java)

Usage
-----

Extract links:

```java
import org.nibor.autolink.*;

String input = "wow, so example: http://test.com";
LinkExtractor linkExtractor = LinkExtractor.builder().build();
Iterable<LinkSpan> links = linkExtractor.extractLinks(input);
LinkSpan link = links.iterator().next();
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
Iterable<LinkSpan> links = linkExtractor.extractLinks(input);
String result = Autolink.renderLinks(input, links, (link, text, sb) -> {
    sb.append("<a href=\"");
    sb.append(text, link.getBeginIndex(), link.getEndIndex());
    sb.append("\">");
    sb.append(text, link.getBeginIndex(), link.getEndIndex());
    sb.append("</a>");
});
result;  // "wow <a href=\"http://test.com\">http://test.com</a> such linked"
```

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

Example input and linked result:

* `http://example.com.` → [http://example.com]().
* `http://example.com,` → [http://example.com](),
* `(http://example.com)` → ([http://example.com]())
* `(... (see http://example.com))` → (... (see [http://example.com]()))
* `https://en.wikipedia.org/wiki/Link_(The_Legend_of_Zelda)` →
  [https://en.wikipedia.org/wiki/Link_(The_Legend_of_Zelda)]()
* `http://üñîçøðé.com/` → [http://üñîçøðé.com/]()

Also see [test cases](src/test/java/org/nibor/autolink/AutolinkUrlTest.java).

### Email address extraction

Extracts emails such as `foo@example.com`. Matches international email
addresses, but doesn't verify the domain name (may match too much).

Examples:

* `foo@example.com` → [foo@example.com]()
* `foo@example.com.` → [foo@example.com]().
* `foo@example.com,` → [foo@example.com](),
* `üñîçøðé@üñîçøðé.com` → [üñîçøðé@üñîçøðé.com]()

Not supported:

* Quoted local parts, e.g. `"this is sparta"@example.com`
* Address literals, e.g. `foo@[127.0.0.1]`

Note that the domain must have at least one dot (e.g. `foo@com` isn't
matched), unless the `emailDomainMustHaveDot` option is disabled.

Also see [test cases](src/test/java/org/nibor/autolink/AutolinkEmailTest.java).

Contributing
------------

Pull requests, issues and comments welcome ☺. For pull requests:

* Add tests for new features and bug fixes
* Follow the existing style (always use braces, 4 space indent)
* Separate unrelated changes into multiple pull requests

License
-------

Copyright (c) 2015-2016 Robin Stocker

MIT licensed, see LICENSE file.
