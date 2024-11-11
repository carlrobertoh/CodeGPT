package ee.carlrobert.codegpt.util

import ee.carlrobert.codegpt.util.StringUtil.findCompletionParts
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class StringUtilTest {

    @Test
    fun `should parse completion without brackets and braces`() {
        val completionLine = "root != null"
        val editorLineSuffix = ") {\n"

        val result = findCompletionParts(editorLineSuffix, completionLine)

        assertThat(result[0].second).isEqualTo(0)
        assertThat(result[0].first).isEqualTo("root != null")
    }

    @Test
    fun `should parse completion with closing bracket and brace into separate parts`() {
        val completionLine = "root != null) {\n"
        val editorLineSuffix = ")\n"

        val result = findCompletionParts(editorLineSuffix, completionLine)

        assertThat(result[0].second).isEqualTo(0)
        assertThat(result[0].first).isEqualTo("root != null")
        assertThat(result[1].second).isEqualTo(1)
        assertThat(result[1].first).isEqualTo(" {")
    }

    @Test
    fun `should parse completion when editor suffix contains closing bracket and brace`() {
        val completionLine = "root != null) {\n"
        val editorLineSuffix = ") {\n"

        val result = findCompletionParts(editorLineSuffix, completionLine)

        assertThat(result[0].second).isEqualTo(0)
        assertThat(result[0].first).isEqualTo("root != null")
    }

    @Test
    fun `should parse completion between opening and closing brackets`() {
        val completionLine = "(root != null) {\n"
        val editorLineSuffix = "() {\n"

        val result = findCompletionParts(editorLineSuffix, completionLine)

        assertThat(result[0].second).isEqualTo(1)
        assertThat(result[0].first).isEqualTo("root != null")
    }
}