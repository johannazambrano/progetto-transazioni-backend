package org.acme.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FiltroRicercaTransactionDTO {
    @Size(max = 100)
    private String title;
    @PositiveOrZero
    private Double amount;
    private String category;
    private LocalDate startDate;
    private LocalDate endDate;
    @Valid
    private PaginazioneDTO paginazione;
}
