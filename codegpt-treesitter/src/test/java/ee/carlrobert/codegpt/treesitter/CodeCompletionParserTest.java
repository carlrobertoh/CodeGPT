package ee.carlrobert.codegpt.treesitter;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class CodeCompletionParserTest {

  @Test
  public void shouldGetValidReturnValue() {
    var prefix = "class Main {\n"
        + "  public int getRandomNumber() {\n"
        + "    return ";
    var suffix = "\n"
        + "  }\n"
        + "}";
    var output = "10;}\n}\npublic int getRandomNumber(int k) {";

    var parsedResponse = CodeCompletionParserFactory
        .getParserForFileExtension("java")
        .parse(prefix, suffix, output);

    assertThat(parsedResponse).isEqualTo("10;");
  }

  @Test
  public void shouldGetValidParenthesisValue() {
    var prefix = "class Main {\n"
        + "  public int getRandomNumber(int ";
    var suffix = ") {\n"
        + "    return 10;\n"
        + "  }\n"
        + "}";
    var output = "prevNumber) {\n    if() {";

    var parsedResponse = CodeCompletionParserFactory
        .getParserForFileExtension("java")
        .parse(prefix, suffix, output);

    assertThat(parsedResponse).isEqualTo("prevNumber");
  }

  @Test
  public void shouldHandleFieldDeclaration() {
    var prefix = "class Main {\n"
        + "\t\n"
        + "    private i";
    var suffix = "\n"
        + "\n"
        + "  public int getRandomNumber(int prevNumber) {\n"
        + "    return Math.of()\n"
        + "  }\n"
        + "}";
    var output = "nt randomNumber;\n"
        + "    \n"
        + "    public void get() {";

    var result = CodeCompletionParserFactory
        .getParserForFileExtension("java")
        .parse(prefix, suffix, output);

    assertThat(result).isEqualTo("nt randomNumber;");
  }

  @Test
  public void shouldHandleFormalParameters() {
    var prefix = "class Main {\n"
        + "  public int getRandomNumber(";
    var suffix = ") {\n"
        + "    return 10;\n"
        + "  }\n"
        + "}";
    var output = "int prevNumber) }";

    var result = CodeCompletionParserFactory
        .getParserForFileExtension("java")
        .parse(prefix, suffix, output);

    assertThat(result).isEqualTo("int prevNumber");
  }
}
