package org.acme.layout.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonDeserialize(builder = FiltroDTO.FiltroDTOBuilder.class)
@RegisterForReflection
public class FiltroDTO {
    @Size(max = 100)
    private String layoutName;
    private Boolean isDefault;


    @JsonPOJOBuilder(withPrefix = "")
    public static class FiltroDTOBuilder {}
}
