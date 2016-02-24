package com.felixmulder.markrat

object HTML {
  sealed trait ParsedHTML

  case class Header(level: Int, inner: String) extends ParsedHTML {
    val innerTrimmed = inner.trim
    override def toString = s"<h$level>$innerTrimmed</h$level>"
  }

  case class Paragraph(body: Seq[Body]) extends ParsedHTML {
    override def toString = s"""<p>${body.mkString(" ")}</p>"""
  }

  sealed trait Body extends ParsedHTML

  case class Text(str: String) extends Body {
    override def toString = str.trim
  }

  case class Bold(inner: Body) extends Body {
    override def toString = s"<b>$inner</b>"
  }

  case class Italic(inner: Body) extends Body {
    override def toString = s"<i>$inner</i>"
  }

  case class Blockquote(inner: Seq[ParsedHTML]) extends Body {
    override def toString = s"<blockquote>${inner.mkString("\n")}</blockquote>"
  }

  case class InlineCode(inner: String) extends Body {
    override def toString = s"<code>$inner</code>"
  }

  case class Code(lang: Option[String], inner: Seq[String]) extends ParsedHTML {
    override def toString =
      s"""<pre><code class="${ lang.getOrElse("") }">${ inner.mkString("") }</code></pre>"""
  }

  case class Image(link: Link) extends Body {
    override def toString =
      s"""<img src="${link.href}" alt="${link.text}" title="${link.hoverText.getOrElse("")}">"""
  }

  case class Link(text: String, href: String, hoverText: Option[String]) extends Body {
    override def toString =
      s"""<a${ hoverText.map(x => s""" title="$x" """).getOrElse(" ") }href="$href">$text</a>"""
  }

  abstract class ListHTML(items: Seq[Seq[ParsedHTML]]) extends Body {
    def tpe: String
    def seqToString(item: Seq[ParsedHTML]): String = item.mkString(" ")
    override def toString =
      s"""<$tpe>${ items.map(seqToString).mkString("<li>", "</li><li>", "</li>") }</$tpe>"""
  }

  case class UnorderedList(items: Seq[Seq[ParsedHTML]]) extends ListHTML(items) {
    override val tpe: String = "ul"
  }

  case class OrderedList(items: Seq[Seq[ParsedHTML]]) extends ListHTML(items) {
    override val tpe: String = "ol"
  }
}
