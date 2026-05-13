package org.acme.transaction.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.acme.api.dto.PaginazioneDTO;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponseDTO {
    @Valid
    private List<TransactionDTO> transactions;
    @NotNull
    private PaginazioneDTO paginazione;
}
