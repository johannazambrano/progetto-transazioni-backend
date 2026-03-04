package org.acme.util.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Paginazione {
    private Integer numeroPagina;
    private Integer risultatiPagina;
    private Integer numeroPagTotali;
    private Long numeroRisTotali;
    private Integer numeroElementiPerPagina;
}
