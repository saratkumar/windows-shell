package com.example.shellscript.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.*;

@Component
public class AppUtils {
    @Autowired
    private ResourceLoader resourceLoader;


    public Resource getFile(String path) {
        Resource resource = resourceLoader.getResource(path);
        if (!resource.exists()) {
            System.err.println("Config file not found at: " + path);
            return null;
        }

        return resource;
    }

    public static void writeLogs(int count, String message) {
        if(count >0) {
            LoggerUtility.logMessage("ERROR: "+ count + " issues reported in "+ message +"\n");
        } else {
            LoggerUtility.logMessage("No issue in " +message +"\n");
        }
    }

    public static InputStream loadYamlInputStream(String path) throws IOException {
        if (path.startsWith("classpath:")) {
            String cleanPath = path.replace("classpath:", "");
            InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(cleanPath);
            if (stream == null) throw new FileNotFoundException("Not found in classpath: " + cleanPath);
            return stream;
        } else {
            return new FileInputStream(path);
        }
    }
}
