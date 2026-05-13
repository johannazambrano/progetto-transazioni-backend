package org.acme.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FiltroRicercaCategoryDTO {
    @Size(max = 100)
    private String descrizione;
    @Valid
    private PaginazioneDTO paginazione;
}