package ee.carlrobert.codegpt.codecompletions.psi.structure.models

data class FieldStructure(
    val name: String,
    val type: ClassName,
    val modifiers: List<String>,
)
