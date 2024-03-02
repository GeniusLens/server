package xyz.thuray.geniuslens.server.data.dto;

import lombok.Data;
import xyz.thuray.geniuslens.server.data.enums.MessageSender;
import xyz.thuray.geniuslens.server.data.enums.MessageType;
import xyz.thuray.geniuslens.server.data.po.MessagePO;

import java.util.List;

@Data
public class TaskParamDTO {
    private String function;
    private List<Long> loraIds;
    private List<String> sourceImages;
}
