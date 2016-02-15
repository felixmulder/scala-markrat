import org.scalatest._

class CombinationTests extends FlatSpec with Matchers {
  import com.felixmulder.markrat._
  import com.felixmulder.markrat.HTML._

  val parser = new MarkdownParser

  "MarkdownParser" should "be able to parse h1 and text" in {
    val expected = Seq(Header(1, "Hello, world!"),
                       Paragraph(Seq(Text("This is a paragraph.. sort of."))))
    testParse("#Hello, world!\nThis is a paragraph.. sort of.", expected)
  }

  it should "be able to parse h1, text then h2" in {
    val expected = Seq(Header(1, "Hello, world!"),
                       Paragraph(Seq(Text("This is a paragraph.. sort of."))),
                       Header(2, "Goodbye, cruel world!"))
    testParse("#Hello, world!\nThis is a paragraph.. sort of.\n\n## Goodbye, cruel world!##", expected)
  }

  it should "be able to parse a multiline paragraph" in {
    val exp = Seq(Paragraph(Seq(Text("Line1"), Text("Line2"), Text("Line3"))))
    testParse("Line1\nLine2\nLine3\n\n", exp)
  }

  it should "be able to handle multiple a mixture of paragraphs and headers" in {
    val expected = Seq(Paragraph(Seq(Text("Line 1"))),
                       Header(1, "Header 1"),
                       Paragraph(Seq(Text("Line 2"), Text("Line 3"))),
                       Header(2, "Header 2"),
                       Paragraph(Seq(Text("Line 4"), Text("Line 5"))))
    testParse("Line 1\n\n# Header 1 #\nLine 2\nLine 3\n\nHeader 2\n---\nLine 4\nLine 5", expected)
  }

  def testParse(str: String, expected: Seq[ParsedHTML]) = parser.parse(str) match {
    case Some(parsed) => parsed shouldEqual expected
    case None => fail
  }
}
