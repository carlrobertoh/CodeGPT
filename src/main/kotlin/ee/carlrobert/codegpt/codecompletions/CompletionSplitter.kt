package ee.carlrobert.codegpt.codecompletions

class CompletionSplitter {
    companion object {
        fun split(completionText: String): String {
            val boundaryPredicates = listOf<(Char) -> Boolean>(
                { Character.isWhitespace(it) },
                { it.isBoundaryCharacter() },
                { !it.isBoundaryCharacter() && !Character.isWhitespace(it) }
            )
            for (predicate in boundaryPredicates) {
                val blockIndex =
                    findContinuousBlock(completionText, predicate)
                if (blockIndex != -1) {
                    return completionText.substring(0, blockIndex)
                }
            }
            return completionText
        }

        private fun findContinuousBlock(
            fullCompletion: String,
            isBoundaryCharacter: BoundaryFinder
        ): Int {
            if (fullCompletion.isEmpty() || !isBoundaryCharacter.isBoundaryCharacter(fullCompletion[0])) {
                return -1
            }

            var endIndex = 0
            while (endIndex < fullCompletion.length
                && isBoundaryCharacter.isBoundaryCharacter(fullCompletion[endIndex])
            ) {
                ++endIndex
            }

            return endIndex
        }

        internal fun interface BoundaryFinder {
            fun isBoundaryCharacter(c: Char): Boolean
        }
    }
}

fun Char?.isBoundaryCharacter(): Boolean = this != null && this in "()[]{}<>~!@#$%^&*-+=|\\;:'\",./?"