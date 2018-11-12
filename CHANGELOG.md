# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).

## [0.10.0] - 2018-11-12
### Changed
- Stop URLs at '`' characters too, same as < and >
- Build and test on Java 11

## [0.9.0] - 2018-06-04
### Added
- Add `extractSpans` method that also returns the text pieces of the
  input before, between and after links. This makes it more convenient
  to write code that transforms the whole input text without having to
  manually keep track of indexes.
### Changed
- Deprecated `Autolink.renderLinks` and `LinkRenderer`, see added (#21)
- Stop URLs when encountering `"`. This is consistent with RFC 3986, and
  it seems unlikely that a user would have an unescaped `"` in an URL
  anyway, as browsers escape it when copying an URL with it. (#21)

## [0.8.0] - 2018-01-10
### Changed
- Add `Automatic-Module-Name` manifest entry so that library can be used
  nicely in Java 9 modules

## [0.7.0] - 2017-08-31
### Changed
- Don't autolink if authority is only "end" characters, e.g. like `http://.` or
  `http://"` (#15)
- Stop URLs at Unicode whitespace characters such as U+00A0 NO-BREAK SPACE,
  thanks @otopba!

## [0.6.0] - 2016-11-07
### Added
- New feature to extract links like `www.example.com` as well (no need for
  `http://`, but must start with `www.`).
  This results a link with type `LinkType.WWW`.
  To opt out of this, specify which link types should be extracted by calling
  the `linkTypes` method on the builder. Thanks to @MTDdk for contributing this!

## [0.5.0] - 2016-06-26
### Changed
- Stop URLs at more invalid characters, notably `<` and `>` (#7). According to
  RFC 3987, angle brackets are not allowed in URLs, and other linkers don't seem
  to allow them either.

## [0.4.0] - 2016-02-11
### Changed
- Treat more special characters as trailing delimiters to not include `">`,
  `"/>` and `");` at the end of URLs (#3)
### Fixed
- Fix unexpected link end with unfinished delimiter pairs in URLs (#5)
- Fix Android incompatibility by not using `java.util.Objects`

## [0.3.0] - 2016-01-16
### Changed
- Stop recognizing "abc://foo" in "1abc://foo". A digit doesn't feel enough like
  a separator, it's more like an invalid scheme.

## [0.2.0] - 2015-06-14
### Changed
- Require domains of emails to have dot by default (multiple parts, e.g.
  `foo@com` is not matched by default).
  Can be disabled by calling `emailDomainMustHaveDot(false)` on builder.

## 0.1.0 - 2015-06-13
### Added
- Initial release!


[0.10.0]: https://github.com/robinst/autolink-java/compare/autolink-0.9.0...autolink-0.10.0
[0.9.0]: https://github.com/robinst/autolink-java/compare/autolink-0.8.0...autolink-0.9.0
[0.8.0]: https://github.com/robinst/autolink-java/compare/autolink-0.7.0...autolink-0.8.0
[0.7.0]: https://github.com/robinst/autolink-java/compare/autolink-0.6.0...autolink-0.7.0
[0.6.0]: https://github.com/robinst/autolink-java/compare/autolink-0.5.0...autolink-0.6.0
[0.5.0]: https://github.com/robinst/autolink-java/compare/autolink-0.4.0...autolink-0.5.0
[0.4.0]: https://github.com/robinst/autolink-java/compare/autolink-0.3.0...autolink-0.4.0
[0.3.0]: https://github.com/robinst/autolink-java/compare/autolink-0.2.0...autolink-0.3.0
[0.2.0]: https://github.com/robinst/autolink-java/compare/autolink-0.1.0...autolink-0.2.0
