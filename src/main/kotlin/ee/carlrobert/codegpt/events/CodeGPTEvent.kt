package ee.carlrobert.codegpt.events

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import ee.carlrobert.codegpt.events.Event.EventType
import java.util.*

data class CodeGPTEvent @JsonCreator constructor(
    @JsonProperty("event") val event: Event
)

@JsonDeserialize(using = EventDeserializer::class)
data class Event @JsonCreator constructor(
    val details: EventDetails?,
    val type: EventType
) {
    enum class EventType {
        ANALYZE_WEB_DOC_STARTED,
        ANALYZE_WEB_DOC_COMPLETED,
        ANALYZE_WEB_DOC_FAILED,
        WEB_SEARCH_ITEM
    }
}

interface EventDetails

data class WebSearchEventDetails(
    val id: UUID,
    val name: String,
    val url: String,
    val displayUrl: String
) : EventDetails

data class AnalysisCompletedEventDetails(
    val id: UUID,
    val name: String,
    val description: String,
    val rerankedResults: List<String>
) : EventDetails

data class AnalysisFailedEventDetails(
    val id: UUID,
    val name: String,
    val description: String,
    val error: String
) : EventDetails

data class DefaultEventDetails(
    val id: UUID,
    val name: String,
    val description: String
) : EventDetails

class EventDeserializer : StdDeserializer<Event>(Event::class.java) {
    private val objectMapper = ObjectMapper().registerKotlinModule()

    override fun deserialize(parser: JsonParser, ctxt: DeserializationContext): Event {
        val node: JsonNode = parser.codec.readTree(parser)
        val type = EventType.valueOf(node.get("type").asText())
        val detailsNode = node.get("details") ?: return Event(null, type)
        val eventDetails = when (type) {
            EventType.WEB_SEARCH_ITEM -> {
                objectMapper.treeToValue(detailsNode, WebSearchEventDetails::class.java)
            }

            EventType.ANALYZE_WEB_DOC_COMPLETED -> {
                objectMapper.treeToValue(detailsNode, AnalysisCompletedEventDetails::class.java)
            }

            EventType.ANALYZE_WEB_DOC_FAILED -> {
                objectMapper.treeToValue(detailsNode, AnalysisFailedEventDetails::class.java)
            }

            else -> {
                objectMapper.treeToValue(detailsNode, DefaultEventDetails::class.java)
            }
        }
        return Event(eventDetails, type)
    }
}
