package com.felixmulder.markrat

object MarkdownTokens {
  val header1 = "#"
  val header2 = "##"
  val header3 = "###"
  val header4 = "####"
  val header5 = "#####"
  val header6 = "######"

  val header1Alt = "={3,}\n".r
  val header2Alt = "-{3,}\n".r

  val boldUnderlines = "__"
  val boldAsterisks = "**"

  val italicsUnderline = "_"
  val italicsAsterisk = "*"

  val innerText = """[^\*_\r\n]+""".r
  val headerText = """[^#\r\n]*""".r
}
