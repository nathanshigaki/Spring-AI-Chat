package dio.budgeting.infrastructure.config;

import dio.budgeting.application.usecase.DeleteTransactionUseCase;
import dio.budgeting.application.usecase.GetFinancialSummaryUseCase;
import dio.budgeting.application.usecase.ListTransactionsUseCase;
import dio.budgeting.application.usecase.RegisterTransactionUseCase;
import dio.budgeting.infrastructure.persistence.repository.JpaChatMemoryRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Configuration
public class SpringAiConfig {

    public static final String CHAT_MEMORY_CONVERSATION_ID = "chat_memory_conversation_id";

    @Bean
    public ChatMemory chatMemory(JpaChatMemoryRepository jpaChatMemoryRepository) {
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(jpaChatMemoryRepository)
                .maxMessages(20)
                .build();
    }

    @Bean
    public ChatClient chatClient(
            ChatClient.Builder chatClientBuilder,
            ChatMemory chatMemory,
            @Value("classpath:prompts/system-message.st") Resource systemPromptResource,
            RegisterTransactionUseCase registerTransactionUseCase,
            GetFinancialSummaryUseCase getFinancialSummaryUseCase,
            ListTransactionsUseCase listTransactionsUseCase,
            DeleteTransactionUseCase deleteTransactionUseCase
    ) throws IOException {
        String systemPrompt = systemPromptResource.getContentAsString(StandardCharsets.UTF_8);

        return chatClientBuilder
                .defaultSystem(systemPrompt)
                .defaultTools(
                        registerTransactionUseCase,
                        getFinancialSummaryUseCase,
                        listTransactionsUseCase,
                        deleteTransactionUseCase
                )
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }
}
