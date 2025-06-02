package com.njdealsandclicks.common.dbinitializer;


public interface EntityInitializer {

    String getEntityName();

    String getYamlName();
    
    String getInitializationVersion();
    
    void initialize();

    int getExecutionOrder();
    
}