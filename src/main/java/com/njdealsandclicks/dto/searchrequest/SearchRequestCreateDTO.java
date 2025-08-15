package com.njdealsandclicks.dto.searchrequest;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SearchRequestCreateDTO {
    
    @NotBlank
    private String input;
    private Integer resultsCount;
    private String path;
    private String ref;
    private String clientTs;
    /*
    ---> clientTs:
    1️⃣ Lo tieni solo come informazione extra
    . Lo salvi in un campo client_ts in tabella, così sai quando l’utente ha fatto la ricerca secondo il suo orologio locale.
    . Utile per debug o analisi, ma non influenza la logica.
    . esempio: entity.setClientTs(dto.getClientTs());   // da creare colonna in entity
    💡 Personalmente, lo terrei come campo separato in DB (client_ts) solo per avere il confronto tra ora lato utente e ora lato server. Così puoi analizzare eventuali ritardi di rete o differenze di fuso orario.
     */
}
