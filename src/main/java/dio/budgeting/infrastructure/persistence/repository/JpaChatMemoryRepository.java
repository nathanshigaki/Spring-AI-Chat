package dio.budgeting.infrastructure.persistence.repository;

import dio.budgeting.infrastructure.persistence.entity.ChatMessageEntity;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.messages.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class JpaChatMemoryRepository implements ChatMemoryRepository {

    private final ChatMessageSpringDataRepository repository;

    public JpaChatMemoryRepository(ChatMessageSpringDataRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<String> findConversationIds() {
        return repository.findAll().stream()
                .map(ChatMessageEntity::getConversationId)
                .distinct()
                .toList();
    }

    @Override
    public List<Message> findByConversationId(String conversationId) {
        var entities = repository.findByConversationIdOrderByCreatedAtAsc(conversationId);
        List<Message> messages = new ArrayList<>();
        for (var entity : entities) {
            String type = entity.getMessageType();
            String content = entity.getContent();
            if (MessageType.USER.name().equalsIgnoreCase(type)) {
                messages.add(new UserMessage(content));
            } else if (MessageType.ASSISTANT.name().equalsIgnoreCase(type)) {
                messages.add(new AssistantMessage(content));
            } else if (MessageType.SYSTEM.name().equalsIgnoreCase(type)) {
                messages.add(new SystemMessage(content));
            }
        }
        return messages;
    }

    @Override
    @Transactional
    public void saveAll(String conversationId, List<Message> messages) {
        repository.deleteByConversationId(conversationId);
        List<ChatMessageEntity> entities = new ArrayList<>();
        for (var msg : messages) {
            entities.add(new ChatMessageEntity(
                    UUID.randomUUID().toString(),
                    conversationId,
                    msg.getMessageType().name(),
                    msg.getText() != null ? msg.getText() : "",
                    Instant.now()
            ));
        }
        repository.saveAll(entities);
    }

    @Override
    @Transactional
    public void deleteByConversationId(String conversationId) {
        repository.deleteByConversationId(conversationId);
    }
}
