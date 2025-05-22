package com.njdealsandclicks.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Getter
@Component
public class PathUtil {
    
    @Value("${custom.init-directory}")
    private String initDirectory;
}
