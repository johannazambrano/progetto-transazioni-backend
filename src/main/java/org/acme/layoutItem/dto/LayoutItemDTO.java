package org.acme.layoutItem.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonDeserialize(builder = LayoutItemDTO.LayoutItemDTOBuilder.class)
@RegisterForReflection
public class LayoutItemDTO {
    private String i;
    private Integer x;
    private Integer y;
    private Integer w;
    private Integer h;
    private Integer minW;
    private Integer maxW;
    private Integer minH;
    private Integer maxH;
    private Boolean staticLayout;

    @JsonPOJOBuilder(withPrefix = "")
    public static class LayoutItemDTOBuilder {}
}
