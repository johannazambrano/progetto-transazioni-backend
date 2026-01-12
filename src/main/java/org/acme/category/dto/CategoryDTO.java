package org.acme.category.dto;

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
@JsonDeserialize(builder=CategoryDTO.CategoryDTOBuilder.class)
@RegisterForReflection
public class CategoryDTO {
    private String id;
    private String descrizione;
    private String codice;

    @JsonPOJOBuilder(withPrefix = "")
    public static class CategoryDTOBuilder {}
}
