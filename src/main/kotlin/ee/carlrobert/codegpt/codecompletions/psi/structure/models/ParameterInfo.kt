package ee.carlrobert.codegpt.codecompletions.psi.structure.models

data class ParameterInfo(
    val name: String,
    val type: ClassName,
    val modifiers: List<String>,
)
