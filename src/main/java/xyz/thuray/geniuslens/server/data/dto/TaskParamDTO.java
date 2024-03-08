package xyz.thuray.geniuslens.server.data.dto;

import lombok.Data;
import xyz.thuray.geniuslens.server.data.enums.MessageSender;
import xyz.thuray.geniuslens.server.data.enums.MessageType;
import xyz.thuray.geniuslens.server.data.po.MessagePO;

import java.util.List;

@Data
public class TaskParamDTO {
    // 推理为1，训练为2
    private Integer taskType;
    private String function;
    private List<Long> loraIds;
    private List<String> sourceImages;
    private String sceneId;
    private Long clothId;
}
