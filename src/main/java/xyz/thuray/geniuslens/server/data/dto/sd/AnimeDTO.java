package xyz.thuray.geniuslens.server.data.dto.sd;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import xyz.thuray.geniuslens.server.data.context.InferenceCtx;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnimeDTO {
    @JsonProperty("init_images")
    private String images;
    @JsonProperty("sd_model_checkpoint")
    private String prompt;
    @JsonProperty("prompt")
    private String model;
    @JsonProperty("task_id")
    private String taskId;

    public static AnimeDTO from(InferenceCtx ctx) {
        return AnimeDTO.builder()
                .images(ctx.getSourceImages().get(0))
                .prompt(ctx.getFunction().getPrompt())
                .model(ctx.getFunction().getName())
                .taskId(ctx.getTask().getTaskId())
                .build();
    }
}
