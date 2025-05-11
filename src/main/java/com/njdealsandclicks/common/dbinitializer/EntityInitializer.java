package com.njdealsandclicks.common.dbinitializer;


public interface EntityInitializer {

    String getEntityName();

    String getYamlPath();
    
    default String getInitializationVersion() { return "1.0"; }
    
    void initialize();
    
}