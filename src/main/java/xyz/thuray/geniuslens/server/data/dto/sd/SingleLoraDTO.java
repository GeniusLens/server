package xyz.thuray.geniuslens.server.data.dto.sd;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import xyz.thuray.geniuslens.server.data.context.InferenceCtx;

@Data
@Builder
public class SingleLoraDTO {
    @JsonProperty("userid")
    private String userid;
    @JsonProperty("images_url")
    private String imagesUrl;
    @JsonProperty("additional_prompt")
    private String additionalPrompt;
    @JsonProperty("makeup_transfer")
    private boolean makeupTransfer;
    @JsonProperty("makeup_transfer_ratio")
    private double makeupTransferRatio;
    @JsonProperty("skin_retouching_bool")
    private boolean skinRetouchingBool;
    @JsonProperty("task_id")
    private String taskId;

    public static SingleLoraDTO fromCtx(InferenceCtx ctx) {
        return SingleLoraDTO.builder()
                // TODO: 切换成真实的userid
//                .userid(ctx.getLoras().get(0).getName())
                .userid("liuyifei2")
                .imagesUrl(ctx.getSourceImages().get(0))
                .additionalPrompt("")
                .makeupTransfer(true)
                .makeupTransferRatio(0.5)
                .skinRetouchingBool(true)
                .taskId(ctx.getTask().getTaskId())
                .build();
    }

}
