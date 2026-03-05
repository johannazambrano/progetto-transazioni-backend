package org.acme.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaginazioneDTO {
    @NotNull
    @Min(0)
    private Integer numeroPagina;
    private Integer risultatiPagina;
    private Integer numeroPagTotali;
    private Long numeroRisTotali;
    @NotNull
    @Min(1)
    private Integer numeroElementiPerPagina;
}