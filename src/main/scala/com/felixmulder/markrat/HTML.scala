package com.felixmulder.markrat

object HTML {
  abstract class ParsedHTML

  case class Header(level: Int, inner: String) extends ParsedHTML {
    val innerTrimmed = inner.trim
    override def toString = s"<h$level>$innerTrimmed</h$level>"
  }

  abstract class InnerHTML extends ParsedHTML
  case class Text(str: String) extends InnerHTML {
    override def toString = str
  }

  case class Bold(inner: InnerHTML) extends InnerHTML {
    val innerTrimmed = inner.toString.trim
    override def toString = s"<b>$innerTrimmed</b>"
  }

  case class Italic(inner: InnerHTML) extends InnerHTML {
    val innerTrimmed = inner.toString.trim
    override def toString = s"<i>$innerTrimmed</i>"
  }

  case class Code(inner: Seq[String]) extends ParsedHTML {
    override def toString =
      s"""<pre><code class="">${ inner.mkString("") }</code></pre>"""
  }
}
