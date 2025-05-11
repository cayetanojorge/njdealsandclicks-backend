package com.njdealsandclicks.util;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;


@Component
public class YamlHashService {

    public String calculateYamlHash(String filePath) throws IOException {
        Resource resource = new ClassPathResource(filePath);
        try (InputStream input = resource.getInputStream()) {
            return DigestUtils.md5DigestAsHex(input);
        }
    }
}