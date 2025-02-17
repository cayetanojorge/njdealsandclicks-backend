package com.njdealsandclicks.util;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.stereotype.Component;

@Component
public class PublicIdGenerator {

    /*
     * Invece di generare e verificare un ID alla volta, possiamo generare un batch di ID (ad esempio, 10 o 20 alla volta) 
     * e verificare la loro unicità con una sola query al database. 
     * Questo approccio riduce significativamente i roundtrip al database.
     * Generare un batch aumenta la probabilità di trovare almeno un ID univoco al primo tentativo
     * Per utilizzare segnare query in repository di rispettiva entità.
     */

    private static final int BATCH_SIZE = 10; // Dimensione del batch
    private static final int DEFAULT_LENGTH = 10;

    // Metodo per generare un singolo PublicId
    private String generatePublicId(String prefix, int length) {
        return prefix + UUID.randomUUID()
                            .toString()
                            .replace("-", "")
                            .substring(0, length);
    }

    // Metodo per generare un batch di PublicId
    public List<String> generatePublicIdBatch(String prefix) {
        return IntStream.range(0, BATCH_SIZE)
                        .mapToObj(i -> generatePublicId(prefix, DEFAULT_LENGTH))
                        .collect(Collectors.toList());
    }

    public List<String> generatePublicIdBatch(String prefix, int customBatch) {
        return IntStream.range(0, customBatch)
                        .mapToObj(i -> generatePublicId(prefix, DEFAULT_LENGTH))
                        .collect(Collectors.toList());
    }
}
