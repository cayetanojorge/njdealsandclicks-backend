package com.njdealsandclicks.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.stereotype.Component;

@Component
public class PublicIdGeneratorService {

    /*
     * Invece di generare e verificare un ID alla volta, possiamo generare un batch di ID (ad esempio, 10 o 20 alla volta) 
     * e verificare la loro unicità con una sola query al database. 
     * Questo approccio riduce significativamente i roundtrip al database.
     * Generare un batch aumenta la probabilità di trovare almeno un ID univoco al primo tentativo
     * Per utilizzare segnare query in repository di rispettiva entità.
     */

    private static final int MAX_ATTEMPTS = 3;
    private static final int BATCH_SIZE = 10; // Dimensione del batch
    private static final int DEFAULT_LENGTH = 10;

    // Metodo per generare un singolo PublicId
    private String generatorPublicId(String prefix, int length) {
        return prefix + UUID.randomUUID()
                            .toString()
                            .replace("-", "")
                            .substring(0, length);
    }

    // Metodo per generare un batch di PublicId
    private List<String> generatePublicIdBatch(String prefix) {
        return IntStream.range(0, BATCH_SIZE)
                        .mapToObj(i -> generatorPublicId(prefix, DEFAULT_LENGTH))
                        .collect(Collectors.toList());
    }

    // // // public List<String> generatePublicIdBatch(String prefix, int customBatch) {
    // // //     return IntStream.range(0, customBatch)
    // // //                     .mapToObj(i -> generatorPublicId(prefix, DEFAULT_LENGTH))
    // // //                     .collect(Collectors.toList());
    // // // }

    // Genera ID univoco chiamando la funzione che filtra quelli esistenti
    private List<String> generatePublicIdsV2(String prefix, Function<List<String>, List<String>> filterAvailablePublicIds) {
        
        for(int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {

            List<String> batchPublicIds = generatePublicIdBatch(prefix);
            List<String> uniquePublicIds = filterAvailablePublicIds.apply(batchPublicIds);
            
            // parsiamo perche' query restituisce lista string di 1 string, contenente tupla che continere tutte le chiavi filtrate
            List<String> uniquePublicIdsParsed = Arrays.asList(uniquePublicIds.get(0)
                                    .replace("(", "")
                                    .replace(")", "")
                                    .split(","));

            // // se non usiamo postgredb, passiamo publicId generate, troviamo quelle presenti in db poi filtraggio in codice
            // Set<String> existingPublicIds = new HashSet<>(findExistingPublicIds.apply(batchPublicIds));
            // List<String> available = batchPublicIds.stream()
            //     .filter(id -> !existingPublicIds.contains(id))
            //     .toList();
            // if (!available.isEmpty()) {
            //     return available;
            // }

            if(!uniquePublicIdsParsed.isEmpty()) {
                return uniquePublicIdsParsed;
            }
        }

        throw new IllegalStateException("Impossibile generare un publicId univoco dopo " + MAX_ATTEMPTS + " tentativi");
    }

    public String generateSinglePublicIdV2(String prefix, Function<List<String>, List<String>> filterAvailablePublicIds) {
        List<String> listPublicIds = generatePublicIdsV2(prefix, filterAvailablePublicIds);
        return listPublicIds.get(0);
    }

    public List<String> generateBatchPublicIdsV2(String prefix, Function<List<String>, List<String>> filterAvailablePublicIds, int nPublicIds) {
        List<String> publicIds = new ArrayList<>();
        while(publicIds.size()<nPublicIds) {
            publicIds.addAll(generatePublicIdsV2(prefix, filterAvailablePublicIds));
        }
        return publicIds;
    }
}
