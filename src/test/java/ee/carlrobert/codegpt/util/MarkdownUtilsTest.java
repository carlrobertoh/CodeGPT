package ee.carlrobert.codegpt.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class MarkdownUtilsTest {

  @Test
  public void shouldExtractMarkdownCodeBlocks() {
    String testInput = "**C++ Code Block**\n"
        + "```cpp\n"
        + "#include <iostream>\n"
        + "\n"
        + "int main() {\n"
        + "    return 0;\n"
        + "}\n"
        + "```\n"
        + "1. We include the **iostream** header file.\n"
        + "2. We define the main function.\n"
        + "\n"
        + "**Java Code Block**\n"
        + "```java\n"
        + "public class Main {\n"
        + "    public static void main(String[] args) {\n"
        + "    }\n"
        + "}\n"
        + "```\n"
        + "1. We define a **public class** called **Main**.\n"
        + "2. We define the **main** method which is the entry point of the program.\n";

    var result = MarkdownUtils.splitCodeBlocks(testInput);

    assertThat(result).containsExactly(
        "**C++ Code Block**\n",
        "```cpp\n"
            + "#include <iostream>\n"
            + "\n"
            + "int main() {\n"
            + "    return 0;\n"
            + "}\n"
            + "```",
        "\n1. We include the **iostream** header file.\n"
            + "2. We define the main function.\n"
            + "\n"
            + "**Java Code Block**\n",
        "```java\n"
            + "public class Main {\n"
            + "    public static void main(String[] args) {\n"
            + "    }\n"
            + "}\n"
            + "```",
        "\n1. We define a **public class** called **Main**.\n"
            + "2. We define the **main** method which is the entry point of the program.\n");
  }

  @Test
  public void shouldExtractMarkdownWithoutCode() {
    String testInput = "**C++ Code Block**\n"
        + "1. We include the **iostream** header file.\n"
        + "2. We define the main function.\n"
        + "\n";

    var result = MarkdownUtils.splitCodeBlocks(testInput);

    assertThat(result).containsExactly(
        "**C++ Code Block**\n"
            + "1. We include the **iostream** header file.\n"
            + "2. We define the main function.\n"
            + "\n");
  }

  @Test
  public void shouldExtractMarkdownCodeOnly() {
    String testInput = "```cpp\n"
        + "#include <iostream>\n"
        + "\n"
        + "int main() {\n"
        + "    return 0;\n"
        + "}\n"
        + "```\n"
        + "```java\n"
        + "public class Main {\n"
        + "    public static void main(String[] args) {\n"
        + "    }\n"
        + "}\n"
        + "```\n";

    var result = MarkdownUtils.splitCodeBlocks(testInput);

    assertThat(result).containsExactly(
        "```cpp\n"
            + "#include <iostream>\n"
            + "\n"
            + "int main() {\n"
            + "    return 0;\n"
            + "}\n"
            + "```",
        "```java\n"
            + "public class Main {\n"
            + "    public static void main(String[] args) {\n"
            + "    }\n"
            + "}\n"
            + "```");
  }
}
