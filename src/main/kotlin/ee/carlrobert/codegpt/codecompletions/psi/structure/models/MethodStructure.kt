package ee.carlrobert.codegpt.codecompletions.psi.structure.models

data class MethodStructure(
    val name: String,
    val returnType: ClassName,
    val parameters: List<ParameterInfo>,
    val modifiers: List<String>,
)
