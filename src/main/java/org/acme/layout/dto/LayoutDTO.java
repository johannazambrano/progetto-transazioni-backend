package org.acme.layout.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.acme.layoutItem.dto.LayoutItemDTO;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonDeserialize(builder = LayoutDTO.LayoutDTOBuilder.class)
@RegisterForReflection
public class LayoutDTO {
    private String id;
    private String layoutName;
    private List<LayoutItemDTO> layoutItems;
    private String updatedAt;
    private Boolean isDefault;

    @JsonPOJOBuilder(withPrefix = "")
    public static class LayoutDTOBuilder {}
}
