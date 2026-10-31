package dio.budgeting.infrastructure.http;

import dio.budgeting.infrastructure.config.SpringAiConfig;
import dio.budgeting.infrastructure.http.dto.ChatMessageRequest;
import dio.budgeting.infrastructure.http.dto.ChatMessageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.ai.audio.transcription.TranscriptionModel;
import org.springframework.ai.audio.tts.TextToSpeechModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/assistant")
@Tag(name = "Assistente Inteligente (IA)", description = "Endpoints para interação multimodal (Texto e Áudio) com memória e execução de ferramentas")
public class AssistantController {

    private final ChatClient chatClient;
    private final TranscriptionModel transcriptionModel;
    private final TextToSpeechModel textToSpeechModel;

    public AssistantController(
            ChatClient chatClient,
            TranscriptionModel transcriptionModel,
            @Autowired(required = false) TextToSpeechModel textToSpeechModel
    ) {
        this.chatClient = chatClient;
        this.transcriptionModel = transcriptionModel;
        this.textToSpeechModel = textToSpeechModel;
    }

    @PostMapping("/chat")
    @Operation(summary = "Conversar via texto", description = "Envia mensagem de texto para o assistente. Suporta histórico contínuo via conversationId.")
    public ChatMessageResponse chat(@RequestBody @Valid ChatMessageRequest request) {
        String convId = (request.conversationId() != null && !request.conversationId().isBlank())
                ? request.conversationId()
                : UUID.randomUUID().toString();

        String response = chatClient.prompt()
                .user(request.message())
                .advisors(advisorSpec -> advisorSpec.param(SpringAiConfig.CHAT_MEMORY_CONVERSATION_ID, convId))
                .call()
                .content();

        return new ChatMessageResponse(response, convId);
    }

    @PostMapping(value = "/audio", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = "audio/mp3")
    @Operation(summary = "Comando por voz (Áudio)", description = "Recebe arquivo de áudio (m4a, mp3, wav), transcreve via Whisper, executa ferramentas e devolve áudio de resposta.")
    public ResponseEntity<Resource> audio(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "conversationId", required = false) String conversationId
    ) {
        String convId = (conversationId != null && !conversationId.isBlank())
                ? conversationId
                : UUID.randomUUID().toString();

        // 1. Transcrição STT (Whisper)
        String transcription = transcriptionModel.transcribe(file.getResource());

        // 2. Raciocínio com LLM + Tool Calling
        String result = chatClient.prompt()
                .user(transcription)
                .advisors(advisorSpec -> advisorSpec.param(SpringAiConfig.CHAT_MEMORY_CONVERSATION_ID, convId))
                .call()
                .content();

        // 3. Síntese de Voz (TTS)
        byte[] audioBytes;
        if (textToSpeechModel != null) {
            audioBytes = textToSpeechModel.call(result);
        } else {
            // Fallback de áudio para ambiente com transcrição e LLM gratuitos
            audioBytes = (result != null ? result : "").getBytes(StandardCharsets.UTF_8);
        }

        var resource = new ByteArrayResource(audioBytes);

        String encodedTranscription = URLEncoder.encode(transcription != null ? transcription : "", StandardCharsets.UTF_8);
        String encodedResponse = URLEncoder.encode(result != null ? result : "", StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename("assistant-response.mp3").build().toString())
                .header("X-Transcription-Text", encodedTranscription)
                .header("X-Assistant-Response", encodedResponse)
                .header("X-Conversation-Id", convId)
                .contentType(MediaType.parseMediaType("audio/mp3"))
                .body(resource);
    }
}
