package dio.budgeting.infrastructure.config;

import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.audio.transcription.AudioTranscriptionResponse;
import org.springframework.ai.audio.transcription.TranscriptionModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;

@Configuration
@Profile("demo")
public class DemoProfileConfig {

    @Bean
    @Primary
    public TranscriptionModel demoTranscriptionModel() {
        return new TranscriptionModel() {
            @Override
            public AudioTranscriptionResponse call(AudioTranscriptionPrompt prompt) {
                return new AudioTranscriptionResponse(null);
            }

            @Override
            public String transcribe(Resource audioResource) {
                return "Gastei 55 reais no supermercado comprando frutas e verduras.";
            }
        };
    }
}
