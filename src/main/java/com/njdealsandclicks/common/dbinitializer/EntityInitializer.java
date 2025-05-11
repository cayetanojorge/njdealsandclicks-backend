package com.njdealsandclicks.common.dbinitializer;


public interface EntityInitializer {

    String getEntityName();

    String getYamlName();
    
    default String getInitializationVersion() { return "1.0"; }
    
    void initialize();
    
}