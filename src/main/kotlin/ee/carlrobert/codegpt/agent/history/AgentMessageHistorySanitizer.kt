package ee.carlrobert.codegpt.agent.history

import ai.koog.prompt.message.Message
import ai.koog.prompt.message.MessagePart

internal object AgentMessageHistorySanitizer {

    fun sanitizeForNewUserTurn(history: List<Message>): List<Message> {
        val sanitized = mutableListOf<Message>()
        val pendingToolCallIds = mutableListOf<String>()
        var firstPendingToolCallIndex: Int? = null
        var anonymousToolCallIndex = 0

        fun discardPendingToolCalls() {
            val truncateFrom = firstPendingToolCallIndex ?: return
            while (sanitized.size > truncateFrom) {
                sanitized.removeAt(sanitized.lastIndex)
            }
            pendingToolCallIds.clear()
            firstPendingToolCallIndex = null
        }

        history.forEach { message ->
            when (message) {
                is Message.Assistant -> {
                    val calls = message.parts.filterIsInstance<MessagePart.Tool.Call>()
                    if (calls.isNotEmpty()) {
                        if (firstPendingToolCallIndex == null) {
                            firstPendingToolCallIndex = sanitized.size
                        }
                        calls.forEach { call ->
                            pendingToolCallIds.add(call.id?.takeIf { it.isNotBlank() }
                                ?: "__anonymous_tool_call_${++anonymousToolCallIndex}")
                        }
                        sanitized.add(message)
                    } else {
                        if (pendingToolCallIds.isNotEmpty()) {
                            discardPendingToolCalls()
                        }
                        sanitized.add(message)
                    }
                }

                is Message.User -> {
                    val results = message.parts.filterIsInstance<MessagePart.Tool.Result>()
                    if (results.isNotEmpty()) {
                        if (pendingToolCallIds.isEmpty()) {
                            return@forEach
                        }
                        results.forEach { res ->
                            if (res.id.isNullOrBlank()) {
                                if (pendingToolCallIds.isNotEmpty()) pendingToolCallIds.removeAt(0)
                            } else {
                                pendingToolCallIds.remove(res.id)
                            }
                        }
                        sanitized.add(message)
                        if (pendingToolCallIds.isEmpty()) {
                            firstPendingToolCallIndex = null
                        }
                    } else {
                        if (pendingToolCallIds.isNotEmpty()) {
                            discardPendingToolCalls()
                        }
                        sanitized.add(message)
                    }
                }

                else -> {
                    if (pendingToolCallIds.isNotEmpty()) {
                        discardPendingToolCalls()
                    }
                    sanitized.add(message)
                }
            }
        }

        if (pendingToolCallIds.isNotEmpty()) {
            discardPendingToolCalls()
        }

        return sanitized
    }

    fun isSafeForNewUserTurn(history: List<Message>): Boolean {
        return sanitizeForNewUserTurn(history).size == history.size
    }
}
