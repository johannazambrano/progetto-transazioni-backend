package org.acme.category.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.acme.api.dto.PaginazioneDTO;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponseDTO {
    @Valid
    private List<CategoryDTO> categories;
    @NotNull
    private PaginazioneDTO paginazione;
}