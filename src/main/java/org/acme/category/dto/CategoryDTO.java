package org.acme.category.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonDeserialize(builder=CategoryDTO.CategoryDTOBuilder.class)
@RegisterForReflection
public class CategoryDTO {
    private String id;
    @NotBlank
    private String descrizione;
    @NotBlank
    private String codice;
    @NotNull
    @PositiveOrZero
    private Double budget;
    private String colore;

    @JsonPOJOBuilder(withPrefix = "")
    public static class CategoryDTOBuilder {}
}
