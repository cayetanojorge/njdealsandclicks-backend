package com.njdealsandclicks.dto.searchrequest;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.njdealsandclicks.util.enums.Market;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SearchRequestCreateDTO {
    
    @NotBlank
    @Size(max = 2000)
    private String input;
    
    @Min(0)
    private Integer resultsCount = 0;
    
    @Size(max = 300)
    private String path;

    @Size(max = 300)
    private String ref;

    @Pattern(
        regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}(:\\d{2})?(\\.\\d+)?(Z|[+\\-]\\d{2}:\\d{2})$",
        message = "clientTs must be ISO-8601, es. 2025-08-15T14:30:00Z"
    )
    private String clientTs;
    
    @NotNull
    private Market market; // "IT", "ES", "UK", "US"

    /*
    ---> clientTs:
    1️⃣ Lo tieni solo come informazione extra
    . Lo salvi in un campo client_ts in tabella, così sai quando l’utente ha fatto la ricerca secondo il suo orologio locale.
    . Utile per debug o analisi, ma non influenza la logica.
    . esempio: entity.setClientTs(dto.getClientTs());   // da creare colonna in entity
    💡 Personalmente, lo terrei come campo separato in DB (client_ts) solo per avere il confronto tra ora lato utente e ora lato server. Così puoi analizzare eventuali ritardi di rete o differenze di fuso orario.
     */
}
