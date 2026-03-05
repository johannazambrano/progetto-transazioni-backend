package org.acme.layout.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.acme.layoutItem.dto.LayoutItemDTO;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonDeserialize(builder = LayoutDTO.LayoutDTOBuilder.class)
@RegisterForReflection
public class LayoutDTO {
    private String id;
    @NotBlank
    private String layoutName;
    @Valid
    private List<LayoutItemDTO> layoutItems;
    private LocalDateTime updatedAt;
    private Boolean isDefault;

    @JsonPOJOBuilder(withPrefix = "")
    public static class LayoutDTOBuilder {}
}
