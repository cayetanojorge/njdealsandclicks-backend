package com.njdealsandclicks.util.enums;

public enum SearchRequestStatus {
    NEW,        // appena salvata, non processata
    PARSED,     // elaborata e interpretata (es. estratti keywords, tags)
    PROCESSED,  // processata completamente
    IGNORED,    // ignorata (es. spam, test, irrilevante)
    ERROR       // errore in parsing o elaborazione
}