package org.acme.transaction.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.acme.category.dto.CategoryDTO;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonDeserialize(builder = TransactionDTO.TransactionDTOBuilder.class)
@RegisterForReflection
public class TransactionDTO {
    private String id;
    public String title;
    public Double amount;
    public CategoryDTO category;
    public String date;

    @JsonPOJOBuilder(withPrefix = "")
    public static class TransactionDTOBuilder{}
}
