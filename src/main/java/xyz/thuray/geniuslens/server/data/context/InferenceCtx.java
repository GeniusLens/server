package xyz.thuray.geniuslens.server.data.context;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import xyz.thuray.geniuslens.server.data.enums.InferenceStatus;
import xyz.thuray.geniuslens.server.data.po.FunctionPO;
import xyz.thuray.geniuslens.server.data.po.LoraPO;
import xyz.thuray.geniuslens.server.data.po.MessagePO;
import xyz.thuray.geniuslens.server.data.po.TaskPO;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InferenceCtx {
    private TaskPO task;
    private InferenceStatus status;
    private FunctionPO function;
    private MessagePO message;
    private List<LoraPO> loras;
    private List<String> sourceImages;

}
