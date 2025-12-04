package chinanko.chinanko.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value; // <--- IMPORTANTE: Agrega este import
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor; // <--- Útil si usas lombok

@Service
public class SentimentAnalysisService {

    // CAMBIO AQUÍ: Inyectamos el valor desde la configuración
    @Value("${azure.cognitive.key}")
    private String apiKey; 

    private static final String ENDPOINT = "https://tonyindustriesuwu.cognitiveservices.azure.com/language/:analyze-text?api-version=2023-04-01";

    // ... (el resto de tu lista BAD_WORDS y variables sigue igual) ...
    private static final List<String> BAD_WORDS = Arrays.asList(
        "idiota", "estupido", "imbecil", "basura", "maldito", 
        "puto", "mierda", "pendejo", "verga", "chinga"
    );

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public TextAnalysisResult analyze(String text) {
        // ... (tu validación de profanity sigue igual) ...
        if (containsProfanity(text)) {
             return new TextAnalysisResult("negative", BigDecimal.ONE, true);
        }

        try {
            // ... (preparación del map document y analysisInput sigue igual) ...
            Map<String, Object> document = Map.of("id", "1", "language", "es", "text", text);
            Map<String, Object> analysisInput = Map.of("documents", List.of(document));
            Map<String, Object> body = Map.of(
                "kind", "SentimentAnalysis",
                "parameters", Map.of("modelVersion", "latest"),
                "analysisInput", analysisInput
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // CAMBIO AQUÍ: Usamos la variable de instancia apiKey (minúscula)
            headers.set("Ocp-Apim-Subscription-Key", apiKey); 

            // ... (el resto del método try/catch sigue igual) ...
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            String response = restTemplate.postForObject(ENDPOINT, request, String.class);
            
            JsonNode root = objectMapper.readTree(response);
            JsonNode docResult = root.path("results").path("documents").get(0);
            
            String sentiment = docResult.path("sentiment").asText();
            double score = docResult.path("confidenceScores").path(sentiment).asDouble();

            return new TextAnalysisResult(sentiment, BigDecimal.valueOf(score), false);

        } catch (Exception e) {
            e.printStackTrace();
            return new TextAnalysisResult("neutral", BigDecimal.ZERO, false);
        }
    }

    // ... (resto de métodos y clase interna siguen igual)
    private boolean containsProfanity(String text) {
        // ... tu código original ...
        if (text == null || text.trim().isEmpty()) return false;
        String normalizedText = text.toLowerCase();
        for (String word : BAD_WORDS) {
            Pattern pattern = Pattern.compile("\\b" + Pattern.quote(word) + "\\b", Pattern.CASE_INSENSITIVE);
            if (pattern.matcher(normalizedText).find()) {
                return true;
            }
        }
        return false;
    }

    @Data
    @AllArgsConstructor
    public static class TextAnalysisResult {
        private String sentiment;
        private BigDecimal score;
        private boolean isOffensive;
    }
}