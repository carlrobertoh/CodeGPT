package ee.carlrobert.codegpt.util

import com.intellij.diff.comparison.ComparisonManager
import com.intellij.diff.comparison.ComparisonPolicy
import com.intellij.diff.comparison.DiffTooBigException
import com.intellij.diff.fragments.LineFragment
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import java.util.regex.Pattern

object DiffUtil {
    fun calculateDifferences(
        editor: Editor,
        originalText: String,
        modifiedText: String
    ): List<LineFragment> {
        try {
            return ProgressManager.getInstance()
                .run(object : Task.WithResult<List<LineFragment>, DiffTooBigException?>(
                    editor.project,
                    "Comparing Lines",
                    true
                ) {
                    @Throws(DiffTooBigException::class)
                    override fun compute(indicator: ProgressIndicator): List<LineFragment> {
                        val replacedFullSource = editor.document.text
                        val originalFullSource = replacedFullSource.replaceFirst(
                            Pattern.quote(modifiedText).toRegex(),
                            originalText
                        )

                        return ComparisonManager.getInstance()
                            .compareLinesInner(
                                originalFullSource,
                                replacedFullSource,
                                ComparisonPolicy.IGNORE_WHITESPACES,
                                indicator
                            )
                    }
                })
        } catch (e: DiffTooBigException) {
            return listOf()
        }
    }
}