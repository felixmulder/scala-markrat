package com.felixmulder.markrat

import scala.util.parsing.combinator.{ PackratParsers, RegexParsers }

class MarkdownParser extends RegexParsers with PackratParsers {
  import HTML._
  import MarkdownTokens._
  import scala.util.matching.Regex
  import scala.language.postfixOps

  lazy val output: PackratParser[Seq[ParsedHTML]] = html*
  lazy val html: PackratParser[ParsedHTML] = header | innerHTML
  lazy val header: PackratParser[Header] = h6 | h5 | h4 | h3 | h2 | h1
  lazy val innerHTML: PackratParser[InnerHTML] = bold | italicized | innerText ^^ Text

  val parseHeader = (level: Int, token: String) =>
    token ~> headerText <~ token ^^ (t => Header(level, t.trim)) |
    token ~> headerText ^^ (t => Header(level, t.trim))

  lazy val h1: PackratParser[Header] =
    parseHeader(1, header1) |
    headerText <~ header1Alt ^^ (t => Header(1, t.trim))

  lazy val h2: PackratParser[Header] =
    parseHeader(2, header2) |
    headerText <~ header2Alt ^^ (t => Header(2, t.trim))

  lazy val h3: PackratParser[Header] = parseHeader(3, header3)
  lazy val h4: PackratParser[Header] = parseHeader(4, header4)
  lazy val h5: PackratParser[Header] = parseHeader(5, header5)
  lazy val h6: PackratParser[Header] = parseHeader(6, header6)

  lazy val bold: PackratParser[Bold] =
    boldUnderlines ~> innerHTML <~ boldUnderlines ^^ Bold |
    boldAsterisks ~> innerHTML <~ boldAsterisks ^^ Bold

  lazy val italicized: PackratParser[Italic] =
    italicsUnderline ~> innerHTML <~ italicsUnderline ^^ Italic |
    italicsAsterisk ~> innerHTML <~ italicsAsterisk ^^ Italic

  def parse(markdown: String) = parseAll(output, markdown) match {
    case Success(result, _) => Some(result)
    case x: NoSuccess => { println(x); None }
  }
}