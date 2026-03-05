package org.acme.api.dto;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FiltroRicercaTransactionDTO {
    private String title;
    private Double amount;
    private String category;
    private LocalDate startDate;
    private LocalDate endDate;
    @Valid
    private PaginazioneDTO paginazione;
}
