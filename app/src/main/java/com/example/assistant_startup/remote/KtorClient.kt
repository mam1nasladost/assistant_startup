package com.example.assistant_startup.remote

import com.example.assistant_startup.domain.models.Chat
import com.example.assistant_startup.domain.models.HintRequest
import com.example.assistant_startup.domain.models.Message
import com.example.assistant_startup.domain.models.SettingsConfig
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.preparePost
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.readUTF8Line
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.SerialName

@Serializable
data class DeepSeekStreamResponse(
    val id: String,
    val choices: List<Choice>,
    val model: String,
    @SerialName("object") val obj: String
) {
    @Serializable
    data class Choice(
        val index: Int,
        val delta: Delta,
        @SerialName("finish_reason") val finishReason: String? = null
    )

    @Serializable
    data class Delta(
        val role: String? = null,
        val content: String? = null
    )
}

@Serializable
data class DeepSeekRequest(
    val model: String,
    val messages: List<MessageRequest>,
    val stream: Boolean
)

@Serializable
data class MessageRequest(
    val role: String,
    val content: String
)

class KtorClient() {
    val client = HttpClient(CIO){
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
            })
        }
        defaultRequest {
//            url("")
            contentType(ContentType.Application.Json)
        }
    }
    private val jsonParser = Json { ignoreUnknownKeys = true }

    fun streamDeepSeek(prompt: String): Flow<String> = flow {
        client.preparePost("https://api.deepseek.com/chat/completions") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer sk-0559425a16b147f498bcbd4079fb911c")
            setBody(
                DeepSeekRequest(
                    model = "deepseek-chat",
                    messages = listOf(MessageRequest(role = "user", content = prompt)),
                    stream = true
                )
            )
        }.execute { response ->
            if (!response.status.isSuccess()) {
                throw Exception("Ошибка API: код ${response.status.value}")
            }

            val channel: ByteReadChannel = response.bodyAsChannel()
            while (!channel.isClosedForRead) {
                val line = channel.readUTF8Line() ?: break
                if (line.startsWith("data:")) {
                    val data = line.substringAfter("data:").trim()
                    if (data == "[DONE]") return@execute

                    try {
                        val chunk = jsonParser.decodeFromString<DeepSeekStreamResponse>(data)
                        val content = chunk.choices.firstOrNull()?.delta?.content
                        if (!content.isNullOrEmpty()) {
                            emit(content)
                        }
                    } catch (e: Exception) {
                        println("Error parsing chunk: ${e.message}")
                    }
                }
            }
        }
    }

    suspend fun sendVoiceCommand(text: String): Result<Unit> = runCatching {
        client.post("commands") {
            setBody(mapOf("command" to text))
        }
    }

    fun streamHints(dto: HintRequest): Flow<String> = flow {
        client.preparePost("api/hints/stream") {
            setBody(dto)
//            header("Authorization", "Bearer $token")
        }.execute { response ->
            val channel: ByteReadChannel = response.bodyAsChannel()

            while (!channel.isClosedForRead) {
                val line = channel.readUTF8Line() ?: break
                if (line.startsWith("data:")) {
                    val data = line.substringAfter("data:").trim()

                    if (data == "[DONE]") {
                        return@execute
                    }

                    if (data.isNotEmpty()) {
                        emit(data)
                    }
                }
            }
        }
    }


    suspend fun toggleLike(messageId: String, isLiked: Boolean): Result<Unit> = runCatching {
        client.post("messages/$messageId/like") {
            setBody(isLiked)
        }
    }

    suspend fun updateConfig(config: SettingsConfig): Result<Unit> = runCatching {
        client.put("settings") {
            setBody(config)
        }
    }

    suspend fun getChats(): Result<List<Chat>> = runCatching {
        client.get("chats").body()
    }

    suspend fun getMessageDetails(messageId: String): Result<Message> = runCatching {
        client.get("messages/$messageId").body()
    }

    fun close() = client.close()

//    suspend fun askDeepSeek(text: String): Result<Unit> = runCatching {
//        client.post("https://api.deepseek.com/chat/completions"){
//            setBody("""{
//                "model": "deepseek-chat",
//                "messages": [
//                {
//                    "role": "user",
//                    "content": "Привет! Расскажи о себе."
//                }
//                ],
//                "stream":true
//            }""")
//        }
//    }
}