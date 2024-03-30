package ee.carlrobert.codegpt.codecompletions

class CompletionSplitter {
    companion object {
        fun split(completionText: String, startOffset: Int, endOffset: Int): String {
            val boundaryPredicates = listOf<(Char) -> Boolean>(
                { Character.isWhitespace(it) },
                { isBoundaryCharacter(it) },
                { !isBoundaryCharacter(it) && !Character.isWhitespace(it) }
            )
            for (predicate in boundaryPredicates) {
                val blockIndex =
                    findContinuousBlock(completionText, endOffset - startOffset, predicate)
                if (blockIndex != -1) {
                    return completionText.substring(0, blockIndex)
                }
            }
            return completionText
        }

        private fun findContinuousBlock(
            fullCompletion: String,
            offset: Int,
            isBoundaryCharacter: BoundaryFinder
        ): Int {
            if (!isBoundaryCharacter.isBoundaryCharacter(fullCompletion[offset])) {
                return -1
            }

            var endIndex = offset
            while (endIndex < fullCompletion.length
                && isBoundaryCharacter.isBoundaryCharacter(fullCompletion[endIndex])
            ) {
                ++endIndex
            }

            return endIndex
        }

        private fun isBoundaryCharacter(c: Char): Boolean = c in "()[]{}<>~!@#$%^&*-+=|\\;:'\",./?"

        internal fun interface BoundaryFinder {
            fun isBoundaryCharacter(c: Char): Boolean
        }
    }
}