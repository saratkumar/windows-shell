package com.example.shellscript;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

@Component
public class ShellCommandExecutor {

    private String command;

    @PostConstruct
    public void runCommandFromConfigFile() {
        try (InputStream input = new ClassPathResource("config.properties").getInputStream()) {
            Properties props = new Properties();
            props.load(input);
            String files = props.getProperty("sh-files");
            if (files == null || files.isEmpty()) {
                System.err.println("Command not found in config.properties");
                return;
            }
            String[] listOfFiles = files.split(",");

            ProcessBuilder builder = new ProcessBuilder();
            // For Unix/macOS
            for(String f: listOfFiles) {
                builder.command("bash", "-c", f);
            }


            // For Windows, use:
//             builder.command("cmd.exe", "/c", command);

            Process process = builder.start();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            String line;
            System.out.println("Output:");
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            BufferedReader errorReader = new BufferedReader(
                    new InputStreamReader(process.getErrorStream()));
            while ((line = errorReader.readLine()) != null) {
                System.err.println("ERROR: " + line);
            }

            int exitCode = process.waitFor();
            System.out.println("Command exited with code: " + exitCode);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
