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
    val expected = Seq(Italic(Text("italic text")))
    testParse("_italic text_", expected)
    testParse("*italic text*", expected)
  }

  it should "be able to parse emphasized from markdown" in {
    val expected = Seq(Bold(Text("bold text")))
    testParse("__bold text__", expected)
    testParse("**bold text**", expected)
  }

  it should "be able to parse emphasized bold from markdown" in {
    val expected = Seq(Bold(Italic(Text("bold and italicized text"))))
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

  "MarkdownParser code" should "be able to handle parsing simple code block" in {
    val expected = Seq(Code(Seq("System.out.println(\"Hello, world!\");\n")))
    testParse("```\nSystem.out.println(\"Hello, world!\");\n```\n", expected)
  }

  it should "be able to handle multiline code block" in {
    val expected = Seq(Code(Seq("int main(void)\n",
                                "{\n",
                                "    printf(\"Hello, world!\");\n",
                                "    return(0);\n",
                                "}\n")))
    testParse("```\nint main(void)\n{\n    printf(\"Hello, world!\");\n    return(0);\n}\n```\n", expected)
  }

  def testParse(str: String, expected: Seq[ParsedHTML]) = parser.parse(str) match {
    case Some(parsed) => assert(parsed === expected)
    case None => fail
  }
}
