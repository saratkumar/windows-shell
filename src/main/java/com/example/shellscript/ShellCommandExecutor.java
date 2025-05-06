package com.example.shellscript;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
//if (os.contains("win")) {
//                        builder.command("cmd.exe", "/c", command);
//                    } else {
//                        builder.command("bash", "-c", command);
//                    }
@Component
public class ShellCommandExecutor {

    @Value("${config.file}")
    private Resource yamlFile;

    private final ProcessBuilder builder = new ProcessBuilder();

    @PostConstruct
    public void readConditionalCommands() throws Exception {
        Yaml yaml = new Yaml();

        try (InputStream input = yamlFile.getInputStream()) {
            Map<String, Object> yamlData = yaml.load(input);

            Map<String, Object> flagsMap = (Map<String, Object>) yamlData.get("flags");

            Map<String, Object> shScriptMap = (Map<String, Object>) yamlData.get("sh-commands");

            List<String> restrictedCommands = (List<String>) yamlData.get("restricted-commands");


            boolean isCommandEnabled = Boolean.TRUE.equals(flagsMap.get("is-command-enabled"));
            boolean isDiffDictEnabled = Boolean.TRUE.equals(flagsMap.get("is-diff-directory-enabled"));
            boolean isDiffFileEnabled = Boolean.TRUE.equals(flagsMap.get("is-diff-files-enabled"));
            boolean isListingEnabled = Boolean.TRUE.equals(flagsMap.get("is-listing-enabled"));
            boolean isChecksumEnabled = Boolean.TRUE.equals(flagsMap.get("is-checksum-enabled"));
            boolean isProcessEnabled = Boolean.TRUE.equals(flagsMap.get("is-processes-enabled"));
            boolean isSshEnabled = Boolean.TRUE.equals(flagsMap.get("is-ssh-list-enabled"));
            ProcessBuilder builder = new ProcessBuilder();
            String os = System.getProperty("os.name").toLowerCase();
            if(isCommandEnabled) {
                List<String> commands = (List<String>) shScriptMap.get("commands");
                for (String command : commands) {
                    builder.command("bash", "-c", command);
                    execute(builder);
                }
            }

        }
    }


    public static boolean containsForbiddenCommand(String input, List<String> forbiddenList) {
        for (String forbidden : forbiddenList) {
            // You can make this more strict with regex or word boundaries if needed
            if (input.matches(".*\\b" + Pattern.quote(forbidden) + "\\b.*")) {
                return true;
            }
        }
        return false;
    }


    public void execute(ProcessBuilder builder) throws IOException, InterruptedException {
        Process process = builder.start();
        process.getInputStream().transferTo(System.out);
        process.getErrorStream().transferTo(System.err);
        int exitCode = process.waitFor();
        System.out.println("Exit code: " + exitCode);
    }
}
