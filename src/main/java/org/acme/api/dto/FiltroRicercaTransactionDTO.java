package org.acme.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FiltroRicercaTransactionDTO {
    private String title;
    private Double amount;
    private String category;
    private String startDate;
    private String endDate;
    private PaginazioneDTO paginazione;
}
