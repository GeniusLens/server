package xyz.thuray.geniuslens.server.data.dto.sd;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import xyz.thuray.geniuslens.server.data.context.InferenceCtx;

import java.util.List;

@Data
@Builder
public class LoraTrainingDTO {
    @JsonProperty("userid")
    private String userId;
    @JsonProperty("images_urls")
    private List<String> imagesUrls;
    @JsonProperty("task_id")
    private String taskId;

    public static LoraTrainingDTO fromCtx(InferenceCtx ctx) {
        return LoraTrainingDTO.builder()
                .userId(ctx.getLora().getName())
                .imagesUrls(ctx.getSourceImages())
                .taskId(ctx.getLora().getName())
                .build();
    }
}
