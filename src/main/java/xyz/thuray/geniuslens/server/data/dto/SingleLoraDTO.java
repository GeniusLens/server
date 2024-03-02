package xyz.thuray.geniuslens.server.data.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

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

    public static SingleLoraDTO demo() {
        return SingleLoraDTO.builder()
                .userid("liuyifei2")
                .imagesUrl("https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fsafe-img.xhscdn.com%2Fbw1%2Fe462a719-6dd0-4551-8812-61e0dc62cfe1%3FimageView2%2F2%2Fw%2F1080%2Fformat%2Fjpg&refer=http%3A%2F%2Fsafe-img.xhscdn.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1711624794&t=f265dd12ca5c3ecf9040af505d1c9b72")
                .additionalPrompt("")
                .makeupTransfer(true)
                .makeupTransferRatio(0.5)
                .skinRetouchingBool(true)
                .build();
    }

}
