package org.acme.api.dto;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FiltroRicercaCategoryDTO {
    private String descrizione;
    @Valid
    private PaginazioneDTO paginazione;
}