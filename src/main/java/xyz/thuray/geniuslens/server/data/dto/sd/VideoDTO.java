package xyz.thuray.geniuslens.server.data.dto.sd;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import xyz.thuray.geniuslens.server.data.context.InferenceCtx;

@Data
@Builder
public class VideoDTO {
    @JsonProperty("user_id")
    private String userId;
    @JsonProperty("init_image_path")
    private String initImageUrl;
    @JsonProperty("last_image_path")
    private String lastImageUrl;
    @JsonProperty("video_path")
    private String videoUrl;
    @JsonProperty("t2v_input_prompt")
    private String t2vPrompt;
    @JsonProperty("scene_id")
    private String sceneId;
    @JsonProperty("max_frames")
    private int maxFrames;
    @JsonProperty("task_id")
    private String taskId;

    public static VideoDTO fromCtxToAnime(InferenceCtx ctx) {
        String loraName = ctx.getLoras().get(0).getName();
        return VideoDTO.builder()
                .userId(loraName)
                .initImageUrl("")
                .lastImageUrl("")
                .videoUrl("")
                .t2vPrompt("")
                .sceneId(ctx.getFunction().getSceneId())
                .maxFrames(16)  // TODO: 使用-1
                .taskId(ctx.getTask().getTaskId())
                .build();
    }

    public static VideoDTO fromCtxToSolo(InferenceCtx ctx) {
        String loraName = ctx.getLoras().get(0).getName();
        return VideoDTO.builder()
                .userId(loraName)
                .initImageUrl(ctx.getSourceImages().get(0))
                .lastImageUrl("")
                .videoUrl("")
                .t2vPrompt("")
                .sceneId("")
                .maxFrames(16)
                .taskId(ctx.getTask().getTaskId())
                .build();
    }

    public static VideoDTO fromCtxToVideo(InferenceCtx ctx) {
        String loraName = ctx.getLoras().get(0).getName();
        return VideoDTO.builder()
                .userId(loraName)
                .initImageUrl("")
                .lastImageUrl("")
                .videoUrl(ctx.getSourceImages().get(0))
                .t2vPrompt("")
                .sceneId("none")
                .maxFrames(16)
                .taskId(ctx.getTask().getTaskId())
                .build();
    }
}
