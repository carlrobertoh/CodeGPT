package ee.carlrobert.codegpt.util

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class MarkdownUtilTest {

  @Test
  fun shouldExtractMarkdownCodeBlocks() {
    val testInput = """
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

            """.trimIndent()

    val result = MarkdownUtil.splitCodeBlocks(testInput)

    assertThat(result).containsExactly("""
            **C++ Code Block**

            """.trimIndent(), """
            ```cpp
            #include <iostream>

            int main() {
                return 0;
            }
            ```""".trimIndent(), """

            1. We include the **iostream** header file.
            2. We define the main function.

            **Java Code Block**

            """.trimIndent(), """
            ```java
            public class Main {
                public static void main(String[] args) {
                }
            }
            ```""".trimIndent(), """

            1. We define a **public class** called **Main**.
            2. We define the **main** method which is the entry point of the program.

            """.trimIndent())
  }

  @Test
  fun shouldExtractMarkdownWithoutCode() {
    val testInput = """
            **C++ Code Block**
            1. We include the **iostream** header file.
            2. We define the main function.



            """.trimIndent()

    val result = MarkdownUtil.splitCodeBlocks(testInput)

    assertThat(result).containsExactly("""
            **C++ Code Block**
            1. We include the **iostream** header file.
            2. We define the main function.



            """.trimIndent())
  }

  @Test
  fun shouldExtractMarkdownCodeOnly() {
    val testInput = """
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

            """.trimIndent()

    val result = MarkdownUtil.splitCodeBlocks(testInput)

    assertThat(result).containsExactly("""
            ```cpp
            #include <iostream>

            int main() {
                return 0;
            }
            ```""".trimIndent(), """
            ```java
            public class Main {
                public static void main(String[] args) {
                }
            }
            ```""".trimIndent())
  }
}
