package ee.carlrobert.codegpt.settings.prompts

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.runInEdt
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.builder.text
import com.intellij.ui.treeStructure.Tree
import com.intellij.util.ui.components.BorderLayoutPanel
import java.awt.Dimension
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.TreePath

class PromptsForm {
    private val root = DefaultMutableTreeNode("Root")
    private val treeModel = DefaultTreeModel(root)

    private val tree = Tree(treeModel).apply {
        isRootVisible = false

        val builtInActions = DefaultMutableTreeNode("Built-In Actions").apply {
            add(DefaultMutableTreeNode("Edit Code"))
            add(DefaultMutableTreeNode("Generate Commit Message"))
        }
        val personas = DefaultMutableTreeNode("Personas").apply {
            add(DefaultMutableTreeNode("CodeGPT Default"))
            add(DefaultMutableTreeNode("Rubber Duck"))
        }
        val chatActions = DefaultMutableTreeNode("Chat Actions").apply {
            add(DefaultMutableTreeNode("Find Bugs"))
            add(DefaultMutableTreeNode("Write Tests"))
            add(DefaultMutableTreeNode("Explain"))
            add(DefaultMutableTreeNode("Refactor"))
            add(DefaultMutableTreeNode("Optimize"))
        }

        root.add(builtInActions)
        root.add(personas)
        root.add(chatActions)

        runInEdt {
            expandPaths(
                listOf(
                    TreePath(builtInActions.path),
                    TreePath(personas.path),
                    TreePath(chatActions.path)
                )
            )
        }
    }

    fun createPanel(): DialogPanel {
        return panel {
            row {
                val toolbarDecorator = ToolbarDecorator.createDecorator(tree)
                    .setAddAction {
                        TODO("To be implemented")
                    }
                    .setRemoveAction {
                        TODO("To be implemented")
                    }
                    .addExtraAction(object :
                        AnAction("Duplicate", "Duplicate prompt", AllIcons.Actions.Copy) {
                        override fun actionPerformed(e: AnActionEvent) {
                            TODO("To be implemented")
                        }
                    })
                    .setRemoveActionUpdater {
                        TODO("To be implemented")
                    }
                    .disableUpDownActions()

                cell(toolbarDecorator.createPanel()).component.apply {
                    preferredSize = Dimension(250, preferredSize.height)
                }
                cell(
                    BorderLayoutPanel()
                        .addToTop(createEditor().component)
                ).align(Align.FILL)

                /*cell(
                    BorderLayoutPanel()
                        .addToTop(createEditor().component)
                        .addToBottom(DialogPanel().apply {
                            panel {
                                row {
                                    textField()
                                        .label("Prompt name:")
                                        .text("New Prompt")
                                }

                                row {
                                    checkBox("Wait for additional user input after invoking")
                                        .comment("If not checked, AI Assistant will answer immediately")
                                }

                                row {
                                    checkBox("Show prompt in 'AI Actions' popup")
                                        .comment("If not checked, the action will be available in chat only")
                                        .selected(true)
                                }
                            }
                        })
                ).align(Align.FILL)*/
            }
        }
    }

    private fun createEditor(): Editor {
        return service<EditorFactory>()
            .run {
                createEditor(createDocument("Explain the following code {{selectedCode}}"))
            }
            .apply {
                settings.additionalLinesCount = 1
                settings.isWhitespacesShown = true
                settings.isLineMarkerAreaShown = false
                settings.isIndentGuidesShown = false
                settings.isLineNumbersShown = false
                settings.isFoldingOutlineShown = false
                settings.isUseSoftWraps = false
                settings.isAdditionalPageAtBottom = false
                settings.isVirtualSpace = false
                settings.setSoftMargins(emptyList<Int>())

                if (this is EditorEx) {
                    setVerticalScrollbarVisible(false)
                }
            }
    }
}
