package xyz.thuray.geniuslens.server.data.dto;

import lombok.Data;
import xyz.thuray.geniuslens.server.data.po.MessagePO;
import xyz.thuray.geniuslens.server.data.vo.Result;

import java.util.List;

@Data
public class InferenceParamDTO {
    private String function;
    private List<Long> loraIds;
    private List<String> sourceImages;

    public static MessagePO toMessage(InferenceParamDTO dto, Long userId) {
        return MessagePO.builder()
                .message(dto.getFunction() + "正在等待推理")
                .senderId(0L)
                .receiverId(userId)
                .type(0)
                .build();
    }
}
