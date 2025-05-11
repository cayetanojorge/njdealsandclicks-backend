package com.njdealsandclicks.common.dbinitializer;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "custom.database.initialization")
@Getter
@Setter
public class DbInitializationProperties {

    private boolean force = false;
    
    private List<String> forceEntities = List.of();
    
    // private String initDirectory = "db-init/develop";
}