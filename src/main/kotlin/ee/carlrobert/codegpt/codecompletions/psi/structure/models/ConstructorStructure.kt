package ee.carlrobert.codegpt.codecompletions.psi.structure.models

data class ConstructorStructure(
    val parameters: List<ParameterInfo>,
    val modifiers: List<String>,
)
