package xyz.thuray.geniuslens.server.data.dto.sd;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import xyz.thuray.geniuslens.server.data.context.InferenceCtx;
import xyz.thuray.geniuslens.server.data.po.LoraPO;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class MultiLoraDTO {
    @JsonProperty("userids")
    private List<String> userIds;
    @JsonProperty("images_url")
    private String image;
    @JsonProperty("task_id")
    private String taskId;

    public static MultiLoraDTO fromCtx(InferenceCtx ctx) {
        return MultiLoraDTO.builder()
                .userIds(ctx.getLoras().stream().map(LoraPO::getName).collect(Collectors.toList()))
                .image(ctx.getSourceImages().get(0))
                .taskId(ctx.getTask().getTaskId())
                .build();
    }
}
