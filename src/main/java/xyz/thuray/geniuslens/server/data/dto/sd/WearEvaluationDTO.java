package xyz.thuray.geniuslens.server.data.dto.sd;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class WearEvaluationDTO {
    @JsonProperty("model")
    private String model;
    @JsonProperty("max_tokens")
    private int maxTokens;
    @JsonProperty("messages")
    private List<MessageDTO> messages;

    public static WearEvaluationDTO fromCtx(String imageUrl) {
        return WearEvaluationDTO.builder()
                .model("gpt-4-vision-preview")
                .maxTokens(512)
                .messages(List.of(MessageDTO.fromImageUrl(imageUrl)))
                .build();
    }

    @Data
    @Builder
    static class MessageDTO {
        @JsonProperty("role")
        private String role;
        @JsonProperty("content")
        private List<ContentDTO> content;

        static MessageDTO fromImageUrl(String imageUrl) {
            return MessageDTO.builder()
                    .role("user")
                    .content(List.of(ContentDTO.fromText("你现在是一个服装设计大师，专门研究服装穿搭，请你对这张图像的穿搭进行评价并且给出一定的建议。"), ContentDTO.fromImageUrl(imageUrl)))
                    .build();
        }
    }

    @Data
    @Builder
    static class ContentDTO {
        @JsonProperty("type")
        private String type;
        @JsonProperty("text")
        private String text;
        @JsonProperty("url")
        private String url;

        static ContentDTO fromImageUrl(String imageUrl) {
            return ContentDTO.builder()
                    .type("image_url")
                    .url(imageUrl)
                    .build();
        }

        static ContentDTO fromText(String text) {
            return ContentDTO.builder()
                    .type("text")
                    .text(text)
                    .build();
        }
    }

}
