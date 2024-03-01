package xyz.thuray.geniuslens.server.data.dto;

import lombok.Data;
import xyz.thuray.geniuslens.server.data.po.SamplePO;

@Data
public class SampleParamDTO {
    private Long functionId;
    private String name;
    // 0: unknown, 1: positive, 2: negative
    private Integer type;
    private String url;

    public static SamplePO toPO(SampleParamDTO dto) {
        return SamplePO.builder()
                .functionId(dto.getFunctionId())
                .name(dto.getName())
                .type(dto.getType())
                .url(dto.getUrl())
                .build();
    }
}
