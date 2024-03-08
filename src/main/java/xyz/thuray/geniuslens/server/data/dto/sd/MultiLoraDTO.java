package xyz.thuray.geniuslens.server.data.dto.sd;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import xyz.thuray.geniuslens.server.data.context.InferenceCtx;

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
                .userIds(List.of("liuyifei","mask1"))
                .image("https://image.thuray.xyz/2024/03/7a0806fe815131850a4b0b5cb8d311e1.png")
                .taskId(ctx.getTask().getTaskId())
                .build();
    }
}
