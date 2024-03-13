package xyz.thuray.geniuslens.server.data.dto.sd;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import xyz.thuray.geniuslens.server.data.context.InferenceCtx;

@Data
@Builder
public class SceneDTO {
    @JsonProperty("scene_id")
    private String sceneId;
    @JsonProperty("userid")
    private String userId;
    @JsonProperty("task_id")
    private String taskId;

    public static SceneDTO fromCtx(InferenceCtx ctx) {
        return SceneDTO.builder()
                .sceneId(ctx.getFunction().getSceneId())
                .userId(ctx.getLoras().get(0).getName())
                .taskId(ctx.getTask().getTaskId())
                .build();
    }
}
