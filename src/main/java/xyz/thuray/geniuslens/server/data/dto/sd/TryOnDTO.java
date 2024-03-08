package xyz.thuray.geniuslens.server.data.dto.sd;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import xyz.thuray.geniuslens.server.data.context.InferenceCtx;

@Data
@Builder
public class TryOnDTO {
    @JsonProperty("cloth_uuid")
    private String clothUuid;
    @JsonProperty("max_train_steps")
    private Integer maxTrainSteps;
    @JsonProperty("template_image")
    private String templateImage;
    @JsonProperty("reference_image")
    private String referenceImage;
    @JsonProperty("dino_text_prompt")
    private String dinoTextPrompt;
    @JsonProperty("task_id")
    private String taskId;

    public static TryOnDTO fromCtx(InferenceCtx ctx) {
        return TryOnDTO.builder()
                .clothUuid(String.valueOf(ctx.getCloth().getId()))
                .maxTrainSteps(200)
                .templateImage(ctx.getSourceImages().get(0))
                .referenceImage(ctx.getSourceImages().get(1))
                .dinoTextPrompt(ctx.getCloth().getPrompt())
                .taskId(ctx.getTask().getTaskId())
                .build();
    }
}
