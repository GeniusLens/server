package xyz.thuray.geniuslens.server.data.context;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import xyz.thuray.geniuslens.server.data.enums.InferenceStatus;
import xyz.thuray.geniuslens.server.data.po.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InferenceCtx {
    private Integer taskType;
    private TaskPO task;
    private LoraPO lora;
    private String sceneId;
    private InferenceStatus status;
    private FunctionPO function;
    private MessagePO message;
    private ClothPO cloth;
    private List<LoraPO> loras;
    private List<String> sourceImages;
}
