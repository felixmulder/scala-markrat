package com.felixmulder.markrat

object MarkdownTokens {
  val EOI = """\z""".r
  val EOL = sys.props("line.separator")

  val header1 = "#"
  val header2 = "##"
  val header3 = "###"
  val header4 = "####"
  val header5 = "#####"
  val header6 = "######"

  val header1Alt = """={3,}""".r
  val header2Alt = """-{3,}""".r

  val boldUnderlines = """__""".r
  val boldAsterisks = """\*\*""".r

  val italicsUnderline = """_""".r
  val italicsAsterisk = """\*""".r

  val unorderedItem = """\*[ ]+""".r
  val orderedItem = """[1-9]+[ ]+""".r

  val codeBlock = "```"
  val codeLanguage = """[a-zA-Z0-9+-]+""".r
  val codeLine = """((?!```\n)[^\r\n]+)[\n]""".r

  val innerText = """[^\*_\r\n]+""".r
  val headerText = """[^#\r\n]*\n?""".r

  val linkText = """[^\]]+""".r
  val url = """[^ )\r\n]+""".r
  val urlTitle = """[^)]""".r
}
