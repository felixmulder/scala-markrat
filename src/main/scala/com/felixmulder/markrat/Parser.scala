package com.felixmulder.markrat

import scala.util.parsing.combinator.{ PackratParsers, RegexParsers }

class MarkdownParser extends RegexParsers with PackratParsers {
  import HTML._
  import MarkdownTokens._
  import scala.util.matching.Regex

  override val skipWhitespace = false
  private val separator = EOI | EOL

  lazy val output: PackratParser[Seq[ParsedHTML]] = html.*
  lazy val html: PackratParser[ParsedHTML] = code | header | horizontalRule | paragraph
  lazy val header: PackratParser[Header] = h6 | h5 | h4 | h3 | h2 | h1
  lazy val innerHTML: PackratParser[Body] =
    unorderedList  |
    orderedList    |
    image          |
    link           |
    inlineCode     |
    bold           |
    italicized     |
    EOL.? ~> innerText ^^ Text

  lazy val paragraph: PackratParser[Paragraph] =
    (blockQuote | innerHTML).+ <~ ((EOL ~ EOL) | EOI) ^^ Paragraph

  val parseHeader = (level: Int, token: String) =>
    token ~> headerText <~ (token.? ~ separator.?) ^^ (t => Header(level, t.trim))

  lazy val h1: PackratParser[Header] =
    parseHeader(1, header1) |
    headerText <~ header1Alt <~ separator.? ^^ (t => Header(1, t.trim))

  lazy val h2: PackratParser[Header] =
    parseHeader(2, header2) |
    headerText <~ header2Alt <~ separator.? ^^ (t => Header(2, t.trim))

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

  lazy val code: PackratParser[Code] =
    (codeBlock ~> codeLanguage.? <~ EOL) ~ codeLine.* <~ codeBlock ~ separator.? ^^ {
      case lang ~ bdy => Code(lang, bdy)
    }

  lazy val inlineCode: PackratParser[InlineCode] =
    "`" ~> inlinedCode <~ "`" ^^ InlineCode

  lazy val link: PackratParser[Link] =
    ("[" ~> linkText <~ "](") ~ url ~ ((whiteSpace.* ~ quote) ~> hoverText <~ quote).? <~ (")" ~ separator.?) ^^ {
      case txt ~ uri ~ hover => Link(txt, uri, hover)
    }

  lazy val image: PackratParser[Image] =
    "!" ~> link ^^ Image

  lazy val blockQuote: PackratParser[Blockquote] =
    ("> " ~> blockContents <~ (EOL | EOI)).+ ^^ {
      case x => Blockquote(x.flatten)
    }

  lazy val blockContents: PackratParser[Seq[ParsedHTML]] =
    (code | header | paragraph).+

  lazy val listParagraph: PackratParser[Paragraph] =
    ("""[ ]+""".r ~> (blockQuote | innerHTML) <~ (EOL | EOI)).+ ^^ Paragraph

  lazy val unorderedList: PackratParser[UnorderedList] =
    (("*" | "+" | "-") ~> (listParagraph <~ EOL.?).+).+ ^^ UnorderedList

  lazy val orderedList: PackratParser[OrderedList] =
    (orderedItem ~> (listParagraph <~ EOL.?).+).+ ^^ OrderedList

  lazy val horizontalRule: PackratParser[HorizontalRule] =
    ("---" | "***" | "___") <~  separator.?  ^^ {
      case _ => HorizontalRule()
    }


  def parse(markdown: String) = parseAll(output, markdown) match {
    case Success(result, _) => Some(result)
    case x: NoSuccess => { println(x); None }
  }
}
