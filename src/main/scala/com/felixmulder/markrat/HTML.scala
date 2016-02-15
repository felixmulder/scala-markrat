package com.felixmulder.markrat

object HTML {
  sealed trait ParsedHTML

  case class Header(level: Int, inner: String) extends ParsedHTML {
    val innerTrimmed = inner.trim
    override def toString = s"<h$level>$innerTrimmed</h$level>"
  }

  sealed trait InnerHTML extends ParsedHTML
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

  case class Blockquote(inner: Seq[ParsedHTML]) extends InnerHTML {
    override def toString = s"<blockquote>${inner.mkString(" ")}</blockquote>"
  }

  case class InlineCode(inner: String) extends InnerHTML {
    override def toString = s"<code>$inner</code>"
  }

  case class Code(lang: Option[String], inner: Seq[String]) extends ParsedHTML {
    override def toString =
      s"""<pre><code class="${ lang.getOrElse("") }">${ inner.mkString("") }</code></pre>"""
  }

  case class Link(text: String, href: String, hoverText: Option[String]) extends ParsedHTML {
    override def toString =
      s"""<a${ hoverText.map(x => s""" title="$x" """).getOrElse(" ") }href="$href">$text</a>"""
  }
}
