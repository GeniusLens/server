package xyz.thuray.geniuslens.server.data.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import xyz.thuray.geniuslens.server.data.po.FunctionPO;

@Data
public class FunctionParamDTO {
    @NotBlank(message = "分类不能为空")
    private String category;
    private String name;
    private String description;
    private String url;
    private String type;
    private Integer peopleCount;

    public static FunctionPO toPO(FunctionParamDTO dto, Long categoryId) {
        return FunctionPO.builder()
                .categoryId(categoryId)
                .name(dto.getName())
                .description(dto.getDescription())
                .url(dto.getUrl())
                .type(dto.getType())
                .peopleCount(dto.getPeopleCount())
                .build();
    }
}
