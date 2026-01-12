package org.acme.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaginazioneDTO {
    private Integer numeroPagina;
    private Integer risultatiPagina;
    private Integer numeroPagTotali;
    private Long numeroRisTotali;
    private Integer numeroElementiPerPagina;
}