import org.scalatest._

class SimpleTests extends FlatSpec with Matchers {
  import com.felixmulder.markrat._
  import com.felixmulder.markrat.HTML._

  val parser = new MarkdownParser

  "MarkdownParser basics" should "be able to parse h1 from markdown" in {
    val expected = Seq(Header(1, "Hello, world!"))
    testParse("#Hello, world! ", expected)
    testParse("# Hello, world! #", expected)
    testParse("Hello, world!\n===\n", expected)
  }

  it should "be able to parse h2 from markdown" in {
    val expected = Seq(Header(2, "Hello, world!"))
    testParse("## Hello, world!", expected)
    testParse("## Hello, world! ##", expected)
    testParse("Hello, world!\n----\n", expected)
  }

  it should "be able to parse bold from markdown" in {
    val expected = Seq(Paragraph(Seq(Italic(Text("italic text")))))
    testParse("_italic text_", expected)
    testParse("*italic text*", expected)
  }

  it should "be able to parse emphasized from markdown" in {
    val expected = Seq(Paragraph(Seq(Bold(Text("bold text")))))
    testParse("__bold text__", expected)
    testParse("**bold text**", expected)
  }

  it should "be able to parse emphasized bold from markdown" in {
    val expected = Seq(Paragraph(Seq(Bold(Italic(Text("bold and italicized text"))))))
    testParse("**_bold and italicized text_**", expected)
  }

  it should "be able to parse multiple headers" in {
    val expected = Seq(Header(1, "Header 1"),
                       Header(2, "Header 2"),
                       Header(3, "Header 3"))
    testParse("# Header 1#\n## Header 2 ##\n### Header 3 ###", expected)
  }

  it should "be able to parse multiple alternate headers" in {
    val expected = Seq(Header(1, "Header 1"),
                       Header(1, "Header 1"),
                       Header(2, "Header 2"),
                       Header(3, "Header 3"))
    testParse("Header 1\n=====\n#Header 1#\nHeader 2\n-------\n### Header 3 ###", expected)
  }

  "Paragraph parsing" should "be able to parse a single line into a paragraph" in {
    val exp = Seq(Paragraph(Seq(Text("Line"))))
    testParse("Line", exp)
    testParse("Line\n\n", exp)
  }

  it should "be able to parse two paragraphs" in {
    val exp = Seq(Paragraph(Seq(Text("Line1"))), Paragraph(Seq(Text("Line2"))))
    testParse("Line1\n\nLine2", exp)
    testParse("Line1\n\nLine2\n\n", exp)
  }

  "Code parsing" should "be able to handle parsing simple code block" in {
    val expected = Seq(Code(None, Seq("System.out.println(\"Hello, world!\");\n")))
    testParse("```\nSystem.out.println(\"Hello, world!\");\n```\n", expected)
  }

  it should "be able to handle multiline code block" in {
    val expected = Seq(Code(None, Seq("int main(void)\n",
                                      "{\n",
                                      "    printf(\"Hello, world!\");\n",
                                      "    return(0);\n",
                                      "}\n")))
    testParse("```\nint main(void)\n{\n    printf(\"Hello, world!\");\n    return(0);\n}\n```\n", expected)
  }

  it should "be able to define a code language if applicable" in {
    val exp = Seq(Code(Some("c"), Seq("printf(\"Hello, world!\");\n")))
    testParse("```c\nprintf(\"Hello, world!\");\n```\n", exp)

    val exp2 = Seq(Code(Some("java"), Seq("public class Test {\n",
                                          "    public static void main(String[] args) {\n",
                                          "        System.out.println(\"Hello, world!\");\n",
                                          "    }\n",
                                          "}\n")))
    testParse("```java\n"+
              "public class Test {\n"+
              "    public static void main(String[] args) {\n"+
              "        System.out.println(\"Hello, world!\");\n"+
              "    }\n"+
              "}\n"+
              "```\n", exp2)
  }

  it should "be able to handle inline code" in {
    val exp = Seq(Paragraph(Seq(InlineCode("scala.collection.mutable"))))
    testParse("`scala.collection.mutable`", exp)
  }

  "Link parsing" should "be able to parse a link" in {
    val exp = Seq(Paragraph(Seq(Link("This is my title!", "http://www.felixmulder.com", None))))

    testParse("[This is my title!](http://www.felixmulder.com)", exp)
  }

  it should "be able to supply a title when necesssary" in {
    /* Need to implement parsing of title
     * Should look like this:
     *
     * [Link text](http://felixmulder.com "Link title")
     */
    val exp = Seq(Paragraph(Seq(Link("This is my title!", "http://www.felixmulder.com", Some("Link title")))))
    testParse("[This is my title!](http://www.felixmulder.com \"Link title\")", exp)
  }

  "Blockquote parsing" should "be able to handle single line block quote" in {
    val exp = Seq(Paragraph(Seq(Blockquote(Seq(Paragraph(Seq(Text("Hello, blockquote!"))))))))
    testParse("> Hello, blockquote!", exp)
  }

  "Unordered list parsing" should "be able to parse a single item of plain text" in {
    val exp = Seq(Paragraph(Seq(UnorderedList(Seq(Seq(Paragraph(Seq(Text("Hello, unordered lists!")))))))))

    testParse("* Hello, unordered lists!", exp)
  }

  it should "be able to parse multiple items of plain text" in {
    val exp = Seq(Paragraph(Seq(
      UnorderedList(Seq(Seq(Paragraph(Seq(Text("Hello, 1")))),
                        Seq(Paragraph(Seq(Text("Hello, 2")))))))))
    testParse("* Hello, 1\n* Hello, 2", exp)
  }

  it should "be able to parse single item with multiple lines of text" in {
    val exp = Seq(Paragraph(Seq(
      UnorderedList(Seq(Seq(Paragraph(Seq(Text("Hello, 1"), Text("Hello, 2")))))))))

    testParse("* Hello, 1\n  Hello, 2", exp)
  }

  it should "be able to parse item with multiple paragraphs" in {
    val exp = Seq(Paragraph(Seq(
      UnorderedList(Seq(Seq(Paragraph(Seq(Text("Hello, 1"))), Paragraph(Seq(Text("Hello, 2")))))))))

    testParse("* Hello, 1\n\n  Hello, 2", exp)
  }

  it should "be able to parse multiple items with multiple paragraphs" in {
    val exp = Seq(Paragraph(Seq(
      UnorderedList(Seq(
        Seq(Paragraph(Seq(Text("Hello, 1"))), Paragraph(Seq(Text("Hello, 2")))),
        Seq(Paragraph(Seq(Text("Hello, 3"))), Paragraph(Seq(Text("Hello, 4"))))
      )))))

    testParse("* Hello, 1\n\n Hello, 2\n* Hello, 3\n\n Hello, 4", exp)
  }

  "Ordered list parsing" should "be able to parse a single item of plain text" in {
    val exp = Seq(Paragraph(Seq(OrderedList(Seq(Seq(Paragraph(Seq(Text("Hello, unordered lists!")))))))))

    testParse("1. Hello, unordered lists!", exp)
  }

  it should "be able to parse multiple items of plain text" in {
    val exp = Seq(Paragraph(Seq(
      OrderedList(Seq(Seq(Paragraph(Seq(Text("Hello, 1")))),
                        Seq(Paragraph(Seq(Text("Hello, 2")))))))))
    testParse("1. Hello, 1\n1. Hello, 2", exp)
  }

  it should "be able to parse single item with multiple lines of text" in {
    val exp = Seq(Paragraph(Seq(
      OrderedList(Seq(Seq(Paragraph(Seq(Text("Hello, 1"), Text("Hello, 2")))))))))

    testParse("1. Hello, 1\n  Hello, 2", exp)
  }

  it should "be able to parse item with multiple paragraphs" in {
    val exp = Seq(Paragraph(Seq(
      OrderedList(Seq(Seq(Paragraph(Seq(Text("Hello, 1"))), Paragraph(Seq(Text("Hello, 2")))))))))

    testParse("1. Hello, 1\n\n  Hello, 2", exp)
  }

  it should "be able to parse multiple items with multiple paragraphs" in {
    val exp = Seq(Paragraph(Seq(
      OrderedList(Seq(
        Seq(Paragraph(Seq(Text("Hello, 1"))), Paragraph(Seq(Text("Hello, 2")))),
        Seq(Paragraph(Seq(Text("Hello, 3"))), Paragraph(Seq(Text("Hello, 4"))))
      )))))

    testParse("1. Hello, 1\n\n Hello, 2\n2. Hello, 3\n\n Hello, 4", exp)
  }

  "Image parsing" should "be able to parse a simple inline image" in {
    val exp = Seq(Paragraph(Seq(Image(Link("text", "href", Some("hoverText"))))))

    testParse("""![text](href "hoverText")""", exp)
  }

  "Horizontal rule parsing" should "be able to parse a simple horizontal rule" in {
    val exp = Seq(HorizontalRule())
    testParse("___", exp)
    testParse("***", exp)
    testParse("---", exp)
  }

  it should "be able to parse an <hr> between two paragraphs" in {
    val exp = Seq(Paragraph(Seq(Text("Hello"))), HorizontalRule(), Paragraph(Seq(Text("Bye"))))

    testParse("Hello\n\n___\nBye", exp)
    testParse("Hello\n\n***\nBye", exp)
    testParse("Hello\n\n---\nBye", exp)
  }

  def testParse(str: String, expected: Seq[ParsedHTML]) = parser.parse(str) match {
    case Some(parsed) => parsed shouldEqual expected
    case None => fail
  }
}
