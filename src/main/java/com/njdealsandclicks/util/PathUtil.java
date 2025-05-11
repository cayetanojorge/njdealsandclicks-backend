package com.njdealsandclicks.util;

import org.springframework.beans.factory.annotation.Value;

import lombok.Getter;

@Getter
public class PathUtil {
    
    @Value("${custom.init-directory}")
    private String initDirectory;
}
