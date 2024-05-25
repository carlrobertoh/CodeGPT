package ee.carlrobert.codegpt.completions

import ee.carlrobert.codegpt.CodeGPTPlugin
import ee.carlrobert.codegpt.completions.llama.PromptTemplate.LLAMA
import ee.carlrobert.codegpt.conversations.ConversationService
import ee.carlrobert.codegpt.conversations.message.Message
import ee.carlrobert.codegpt.settings.configuration.ConfigurationSettings
import ee.carlrobert.llm.client.http.RequestEntity
import ee.carlrobert.llm.client.http.exchange.NdJsonStreamHttpExchange
import ee.carlrobert.llm.client.http.exchange.StreamHttpExchange
import ee.carlrobert.llm.client.ollama.OllamaClient
import ee.carlrobert.llm.client.ollama.completion.request.OllamaCompletionRequest
import ee.carlrobert.llm.client.ollama.completion.request.OllamaParameters
import ee.carlrobert.llm.client.util.JSONUtil.*
import ee.carlrobert.llm.completion.CompletionEventListener
import okhttp3.sse.EventSource
import org.apache.http.HttpHeaders
import org.assertj.core.api.Assertions.assertThat
import testsupport.IntegrationTest

class DefaultCompletionRequestHandlerTest : IntegrationTest() {

  fun testOpenAIChatCompletionCall() {
    useOpenAIService()
    ConfigurationSettings.getCurrentState().systemPrompt = "TEST_SYSTEM_PROMPT"
    val message = Message("TEST_PROMPT")
    val conversation = ConversationService.getInstance().startConversation()
    val requestHandler = CompletionRequestHandler(getRequestEventListener(message))
    expectOpenAI(StreamHttpExchange { request: RequestEntity ->
      assertThat(request.uri.path).isEqualTo("/v1/chat/completions")
      assertThat(request.method).isEqualTo("POST")
      assertThat(request.headers[HttpHeaders.AUTHORIZATION]!![0]).isEqualTo("Bearer TEST_API_KEY")
      assertThat(request.body)
        .extracting(
          "model",
          "messages")
        .containsExactly(
          "gpt-4",
          listOf(
            mapOf("role" to "system", "content" to "TEST_SYSTEM_PROMPT"),
            mapOf("role" to "user", "content" to "TEST_PROMPT")))
      listOf(
        jsonMapResponse("choices", jsonArray(jsonMap("delta", jsonMap("role", "assistant")))),
        jsonMapResponse("choices", jsonArray(jsonMap("delta", jsonMap("content", "Hel")))),
        jsonMapResponse("choices", jsonArray(jsonMap("delta", jsonMap("content", "lo")))),
        jsonMapResponse("choices", jsonArray(jsonMap("delta", jsonMap("content", "!")))))
    })

    requestHandler.call(CallParameters(conversation, ConversationType.DEFAULT, message, false))

    waitExpecting { "Hello!" == message.response }
  }

  fun testAzureChatCompletionCall() {
    useAzureService()
    ConfigurationSettings.getCurrentState().systemPrompt = "TEST_SYSTEM_PROMPT"
    val conversationService = ConversationService.getInstance()
    val prevMessage = Message("TEST_PREV_PROMPT")
    prevMessage.response = "TEST_PREV_RESPONSE"
    val conversation = conversationService.startConversation()
    conversation.addMessage(prevMessage)
    conversationService.saveConversation(conversation)
    expectAzure(StreamHttpExchange { request: RequestEntity ->
      assertThat(request.uri.path).isEqualTo(
        "/openai/deployments/TEST_DEPLOYMENT_ID/chat/completions")
      assertThat(request.uri.query).isEqualTo("api-version=TEST_API_VERSION")
      assertThat(request.headers["Api-key"]!![0]).isEqualTo("TEST_API_KEY")
      assertThat(request.headers["X-llm-application-tag"]!![0]).isEqualTo("codegpt")
      assertThat(request.body)
        .extracting("messages")
        .isEqualTo(
          listOf(
            mapOf("role" to "system", "content" to "TEST_SYSTEM_PROMPT"),
            mapOf("role" to "user", "content" to "TEST_PREV_PROMPT"),
            mapOf("role" to "assistant", "content" to "TEST_PREV_RESPONSE"),
            mapOf("role" to "user", "content" to "TEST_PROMPT")))
      listOf(
        jsonMapResponse("choices", jsonArray(jsonMap("delta", jsonMap("role", "assistant")))),
        jsonMapResponse("choices", jsonArray(jsonMap("delta", jsonMap("content", "Hel")))),
        jsonMapResponse("choices", jsonArray(jsonMap("delta", jsonMap("content", "lo")))),
        jsonMapResponse("choices", jsonArray(jsonMap("delta", jsonMap("content", "!")))))
    })
    val message = Message("TEST_PROMPT")
    val requestHandler = CompletionRequestHandler(getRequestEventListener(message))

    requestHandler.call(CallParameters(conversation, ConversationType.DEFAULT, message, false))

    waitExpecting { "Hello!" == message.response }
  }

  fun testYouChatCompletionCall() {
    useYouService()
    val message = Message("TEST_PROMPT")
    val conversation = ConversationService.getInstance().startConversation()
    conversation.addMessage(Message("Ping", "Pong"))
    val requestHandler = CompletionRequestHandler(getRequestEventListener(message))
    expectYou(StreamHttpExchange { request: RequestEntity ->
      assertThat(request.uri.path).isEqualTo("/api/streamingSearch")
      assertThat(request.method).isEqualTo("GET")
      assertThat(request.uri.path).isEqualTo("/api/streamingSearch")
      assertThat(request.uri.query).isEqualTo(
        "q=TEST_PROMPT&"
                + "page=1&"
                + "cfr=CodeGPT&"
                + "count=10&"
                + "safeSearch=WebPages,Translations,TimeZone,Computation,RelatedSearches&"
                + "domain=youchat&"
                + "selectedChatMode=default&"
                + "chat=[{\"question\":\"Ping\",\"answer\":\"Pong\"}]&"
                + "utm_source=ide&"
                + "utm_medium=jetbrains&"
                + "utm_campaign=" + CodeGPTPlugin.getVersion() + "&"
                + "utm_content=CodeGPT")
      assertThat(request.headers)
        .flatExtracting("Accept", "Connection", "User-agent", "Cookie")
        .containsExactly(
          "text/event-stream",
          "Keep-Alive",
          "youide CodeGPT",
          "safesearch_guest=Moderate; "
                  + "youpro_subscription=true; "
                  + "you_subscription=free; "
                  + "stytch_session=; "
                  + "ydc_stytch_session=; "
                  + "stytch_session_jwt=; "
                  + "ydc_stytch_session_jwt=; "
                  + "eg4=false; "
                  + "__cf_bm=aN2b3pQMH8XADeMB7bg9s1bJ_bfXBcCHophfOGRg6g0-1693601599-0-"
                  + "AWIt5Mr4Y3xQI4mIJ1lSf4+vijWKDobrty8OopDeBxY+NABe0MRFidF3dCUoWjRt8"
                  + "SVMvBZPI3zkOgcRs7Mz3yazd7f7c58HwW5Xg9jdBjNg;")
      listOf(
        jsonMapResponse("youChatToken", "Hel"),
        jsonMapResponse("youChatToken", "lo"),
        jsonMapResponse("youChatToken", "!"))
    })

    requestHandler.call(CallParameters(conversation, ConversationType.DEFAULT, message, false))

    waitExpecting { "Hello!" == message.response }
  }

  fun testLlamaChatCompletionCall() {
    useLlamaService()
    ConfigurationSettings.getCurrentState().maxTokens = 99
    ConfigurationSettings.getCurrentState().systemPrompt = "TEST_SYSTEM_PROMPT"
    val message = Message("TEST_PROMPT")
    val conversation = ConversationService.getInstance().startConversation()
    conversation.addMessage(Message("Ping", "Pong"))
    val requestHandler = CompletionRequestHandler(getRequestEventListener(message))
    expectLlama(StreamHttpExchange { request: RequestEntity ->
      assertThat(request.uri.path).isEqualTo("/completion")
      assertThat(request.body)
        .extracting(
          "prompt",
          "n_predict",
          "stream")
        .containsExactly(
          LLAMA.buildPrompt(
            "TEST_SYSTEM_PROMPT",
            "TEST_PROMPT",
            conversation.messages),
          99,
          true)
      listOf<String?>(
        jsonMapResponse("content", "Hel"),
        jsonMapResponse("content", "lo!"),
        jsonMapResponse(
          e("content", ""),
          e("stop", true)))
    })

    requestHandler.call(CallParameters(conversation, ConversationType.DEFAULT, message, false))

    waitExpecting { "Hello!" == message.response }
  }

  fun testOllamaChatCompletionCall() {
    useOllamaService()
    ConfigurationSettings.getCurrentState().maxTokens = 99
    ConfigurationSettings.getCurrentState().systemPrompt = "TEST_SYSTEM_PROMPT"
    val message = Message("TEST_PROMPT")
    val conversation = ConversationService.getInstance().startConversation()
    val requestHandler = CompletionRequestHandler(getRequestEventListener(message))
    expectOllama(NdJsonStreamHttpExchange { request: RequestEntity ->
      assertThat(request.uri.path).isEqualTo("/api/chat")
      assertThat(request.headers[HttpHeaders.AUTHORIZATION]!![0]).isEqualTo("Bearer TEST_API_KEY")
      assertThat(request.body)
        .extracting(
          "model",
          "messages",
          "options.num_predict",
          "stream"
        )
        .containsExactly(
          HuggingFaceModel.LLAMA_3_8B_Q6_K.code,
          listOf(
            mapOf("role" to "system", "content" to "TEST_SYSTEM_PROMPT"),
            mapOf("role" to "user", "content" to "TEST_PROMPT")
          ),
          99,
          true
        )
      listOf(
        jsonMapResponse("message", jsonMap(e("content", "Hel"), e("role", "assistant"))),
        jsonMapResponse("message", jsonMap(e("content", "lo"), e("role", "assistant"))),
        jsonMapResponse("message", jsonMap(e("content", "!"), e("role", "assistant")))
      )
    })

    requestHandler.call(CallParameters(conversation, ConversationType.DEFAULT, message, false))
    waitExpecting { "Hello!" == message.response }
  }

  fun testGoogleChatCompletionCall() {
    useGoogleService()
    ConfigurationSettings.getCurrentState().systemPrompt = "TEST_SYSTEM_PROMPT"
    val message = Message("TEST_PROMPT")
    val conversation = ConversationService.getInstance().startConversation()
    val requestHandler = CompletionRequestHandler(getRequestEventListener(message))
    expectGoogle(StreamHttpExchange { request: RequestEntity ->
      assertThat(request.uri.path).isEqualTo("/v1/models/gemini-pro:streamGenerateContent")
      assertThat(request.method).isEqualTo("POST")
      assertThat(request.uri.query).isEqualTo("key=TEST_API_KEY&alt=sse")
      assertThat(request.body)
        .extracting("contents")
        .isEqualTo(
          listOf(
          mapOf("parts" to listOf(mapOf("text" to "TEST_SYSTEM_PROMPT")), "role" to "user"),
          mapOf("parts" to listOf(mapOf("text" to "Understood.")), "role" to "model"),
          mapOf("parts" to listOf(mapOf("text" to "TEST_PROMPT")), "role" to "user"),
          )
        )
      listOf(
        jsonMapResponse(
          "candidates",
          jsonArray(jsonMap("content", jsonMap("parts", jsonArray(jsonMap("text", "Hello")))))
        ),
        jsonMapResponse(
          "candidates",
          jsonArray(jsonMap("content", jsonMap("parts", jsonArray(jsonMap("text", "!")))))
        )
      )
    })

    requestHandler.call(CallParameters(conversation, ConversationType.DEFAULT, message, false))

    waitExpecting { "Hello!" == message.response }
  }

  fun testCodeGPTServiceChatCompletionCall() {
    useCodeGPTService()
    ConfigurationSettings.getCurrentState().systemPrompt = "TEST_SYSTEM_PROMPT"
    val message = Message("TEST_PROMPT")
    val conversation = ConversationService.getInstance().startConversation()
    val requestHandler = CompletionRequestHandler(getRequestEventListener(message))
    expectCodeGPT(StreamHttpExchange { request: RequestEntity ->
      assertThat(request.uri.path).isEqualTo("/v1/chat/completions")
      assertThat(request.method).isEqualTo("POST")
      assertThat(request.headers[HttpHeaders.AUTHORIZATION]!![0]).isEqualTo("Bearer TEST_API_KEY")
      assertThat(request.body)
        .extracting(
          "model",
          "messages")
        .containsExactly(
          "TEST_MODEL",
          listOf(
            mapOf("role" to "system", "content" to "TEST_SYSTEM_PROMPT"),
            mapOf("role" to "user", "content" to "TEST_PROMPT")))
      listOf(
        jsonMapResponse("choices", jsonArray(jsonMap("delta", jsonMap("role", "assistant")))),
        jsonMapResponse("choices", jsonArray(jsonMap("delta", jsonMap("content", "Hel")))),
        jsonMapResponse("choices", jsonArray(jsonMap("delta", jsonMap("content", "lo")))),
        jsonMapResponse("choices", jsonArray(jsonMap("delta", jsonMap("content", "!")))))
    })

    requestHandler.call(CallParameters(conversation, ConversationType.DEFAULT, message, false))

    waitExpecting { "Hello!" == message.response }
  }

  private fun getRequestEventListener(message: Message): CompletionResponseEventListener {
    return object : CompletionResponseEventListener {
      override fun handleCompleted(fullMessage: String, callParameters: CallParameters) {
        message.response = fullMessage
      }
    }
  }
}
