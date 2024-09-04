package gg.falmer;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.util.concurrent.RateLimiter;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class CrptApi {
    private final WebClient webClient;
    // Вместо RateLimiter можно использовать другие аналоги, т.к. этот помечен @Beta
    private final RateLimiter rateLimiter;

    public CrptApi(TimeUnit timeUnit, int requestLimit) {
        this.webClient = WebClient.builder()
                .baseUrl("https://ismp.crpt.ru")
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .build();

        double permitsPerSecond = (double) requestLimit / timeUnit.toSeconds(1);

        this.rateLimiter = RateLimiter.create(permitsPerSecond);
    }

    public Mono<String> createDocument(Document document, String signature) {
        return Mono.defer(() -> {
            rateLimiter.acquire();  // блокируется, если лимит превышен
            return webClient.post()
                    .uri("/api/v3/lk/documents/create")
                    .bodyValue(document)
                    .retrieve()
                    .bodyToMono(String.class);
        });
    }

    @Getter
    @Setter
    public static class Document {
        private String description;
        @JsonProperty("doc_id")
        private String docId;
        @JsonProperty("doc_status")
        private String docStatus;
        @JsonProperty("doc_type")
        private String docType;
        @JsonProperty("import_request")
        private boolean importRequest;
        @JsonProperty("owner_inn")
        private String ownerInn;
        @JsonProperty("participant_inn")
        private String participantInn;
        @JsonProperty("producer_inn")
        private String producerInn;
        @JsonFormat(pattern = "yyyy-MM-dd")
        @JsonProperty("production_date")
        private LocalDate productionDate;
        @JsonProperty("production_type")
        private String productionType;
        private List<Product> products;
        @JsonFormat(pattern = "yyyy-MM-dd")
        @JsonProperty("reg_date")
        private LocalDate regDate;
        @JsonProperty("reg_number")
        private String regNumber;
    }

    @Getter
    @Setter
    public static class Product {
        @JsonProperty("certificate_document")
        private String certificateDocument;
        @JsonFormat(pattern = "yyyy-MM-dd")
        @JsonProperty("certificate_document_date")
        private LocalDate certificateDocumentDate;
        @JsonProperty("certificate_document_number")
        private String certificateDocumentNumber;
        @JsonProperty("owner_inn")
        private String ownerInn;
        @JsonProperty("producer_inn")
        private String producerInn;
        @JsonFormat(pattern = "yyyy-MM-dd")
        @JsonProperty("production_date")
        private LocalDate productionDate;
        @JsonProperty("tnved_code")
        private String tnvedCode;
        @JsonProperty("uit_code")
        private String uitCode;
        @JsonProperty("uitu_code")
        private String uituCode;
    }
}
