package ee.carlrobert.codegpt.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class MarkdownUtilTest {

  @Test
  public void shouldExtractMarkdownCodeBlocks() {
    String testInput = """
            **C++ Code Block**
            ```cpp
            #include <iostream>

            int main() {
                return 0;
            }
            ```
            1. We include the **iostream** header file.
            2. We define the main function.

            **Java Code Block**
            ```java
            public class Main {
                public static void main(String[] args) {
                }
            }
            ```
            1. We define a **public class** called **Main**.
            2. We define the **main** method which is the entry point of the program.
            """;

    var result = MarkdownUtil.splitCodeBlocks(testInput);

    assertThat(result).containsExactly("""
            **C++ Code Block**
            """, """
            ```cpp
            #include <iostream>

            int main() {
                return 0;
            }
            ```""", """

            1. We include the **iostream** header file.
            2. We define the main function.

            **Java Code Block**
            """, """
            ```java
            public class Main {
                public static void main(String[] args) {
                }
            }
            ```""", """

            1. We define a **public class** called **Main**.
            2. We define the **main** method which is the entry point of the program.
            """);
  }

  @Test
  public void shouldExtractMarkdownWithoutCode() {
    String testInput = """
            **C++ Code Block**
            1. We include the **iostream** header file.
            2. We define the main function.

            """;

    var result = MarkdownUtil.splitCodeBlocks(testInput);

    assertThat(result).containsExactly("""
            **C++ Code Block**
            1. We include the **iostream** header file.
            2. We define the main function.

            """);
  }

  @Test
  public void shouldExtractMarkdownCodeOnly() {
    String testInput = """
            ```cpp
            #include <iostream>

            int main() {
                return 0;
            }
            ```
            ```java
            public class Main {
                public static void main(String[] args) {
                }
            }
            ```
            """;

    var result = MarkdownUtil.splitCodeBlocks(testInput);

    assertThat(result).containsExactly("""
            ```cpp
            #include <iostream>

            int main() {
                return 0;
            }
            ```""", """
            ```java
            public class Main {
                public static void main(String[] args) {
                }
            }
            ```""");
  }
}
