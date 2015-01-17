import org.scalatest._

class SimpleTests extends FlatSpec with Matchers {
  import com.felixmulder.markrat._
  import com.felixmulder.markrat.HTML._

  val parser = new MarkdownParser

  "MarkdownParser" should "be able to parse h1 from markdown" in {
    val expected = Header(1, "Hello, world!")
    testParse("#Hello, world! ", expected)
    testParse("# Hello, world! #", expected)
    testParse("Hello, world!\n===\n", expected)
  }

  it should "be able to parse h2 from markdown" in {
    val expected = Header(2, "Hello, world!")
    testParse("## Hello, world!", expected)
    testParse("## Hello, world! ##", expected)
    testParse("Hello, world!\n----\n", expected)
  }

  it should "be able to parse bold from markdown" in {
    val expected = Italic(Text("italic text"))
    testParse("_italic text_", expected)
    testParse("*italic text*", expected)
  }

  it should "be able to parse emphasized from markdown" in {
    val expected = Bold(Text("bold text"))
    testParse("__bold text__", expected)
    testParse("**bold text**", expected)
  }

  it should "Be able to parse emphasized bold from markdown" in {
    val expected = Bold(Italic(Text("bold and italicized text")))
    testParse("**_bold and italicized text_**", expected)
  }

  def testParse(str: String, expected: ParsedHTML) = parser.parse(str) match {
    case Some(parsed) => assert(parsed === expected)
    case None => fail
  }
}
