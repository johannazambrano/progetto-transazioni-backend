package org.acme.transaction.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.acme.category.dto.CategoryDTO;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonDeserialize(builder = TransactionDTO.TransactionDTOBuilder.class)
@RegisterForReflection
public class TransactionDTO {
    private String id;
    @NotBlank
    private String title;
    @NotNull
    @Positive
    private Double amount;
    @Valid
    private CategoryDTO category;
    @NotNull
    private LocalDate date;

    @JsonPOJOBuilder(withPrefix = "")
    public static class TransactionDTOBuilder{}
}
