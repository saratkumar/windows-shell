package com.example.shellscript;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;
import org.springframework.core.io.Resource;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Component
public class ShellCommandExecutor {

    @Value("${config.file}")
    private Resource yamlFile;

    private final ProcessBuilder builder = new ProcessBuilder();



    String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));

    String logFileName = "command_" + timestamp + ".log";

    String summaryLogFile = "summary_log_" + timestamp + ".log";

    List<String> restrictedCommands = new ArrayList<>();

    File log = null;

    @PostConstruct
    public void readConditionalCommands() throws Exception {
        Yaml yaml = new Yaml();
        File logDir = new File("logs");
        if (!logDir.exists()) {
            logDir.mkdirs();
        }
        log = new File(logDir, logFileName);
        File summaryLog = new File(logDir, summaryLogFile);
        try (
                InputStream input = yamlFile.getInputStream();
                PrintWriter writer = new PrintWriter(new FileWriter(summaryLog, true))
        ) {
            Map<String, Object> yamlData = yaml.load(input);

            Map<String, Object> flagsMap = (Map<String, Object>) yamlData.get("flags");

            Map<String, Object> shScriptMap = (Map<String, Object>) yamlData.get("sh-commands");

            restrictedCommands = (List<String>) yamlData.get("restricted-commands");

            boolean isCommandEnabled = Boolean.TRUE.equals(flagsMap.get("is-command-enabled"));
            boolean isDiffDictEnabled = Boolean.TRUE.equals(flagsMap.get("is-diff-directory-enabled"));
            boolean isDiffFileEnabled = Boolean.TRUE.equals(flagsMap.get("is-diff-files-enabled"));
            boolean isListingEnabled = Boolean.TRUE.equals(flagsMap.get("is-listing-enabled"));
            boolean isChecksumEnabled = Boolean.TRUE.equals(flagsMap.get("is-checksum-enabled"));
            boolean isProcessEnabled = Boolean.TRUE.equals(flagsMap.get("is-processes-enabled"));
            boolean isSshEnabled = Boolean.TRUE.equals(flagsMap.get("is-ssh-list-enabled"));

            PrintStream logStream = new PrintStream(new FileOutputStream(log, true));
            System.setOut(logStream);
            writer.println("Linux File System  Executing Commands:");
            int count = 0;
            if(isCommandEnabled) {
                List<String> commands = (List<String>) shScriptMap.get("commands");
                for (String command : commands) {
                    Boolean isExecuted = execute(command);
                    if(!isExecuted) {
                        count++;
                    }
                }
                writeLogs(count, "executing commands", writer);
            }

            if(isListingEnabled) {
                count = 0;
                List<String> listings = (List<String>) shScriptMap.get("listings");
                for (String directory: listings) {
                    Boolean isExecuted = execute("ls -ltr "+directory);
                    if(!isExecuted) {
                        count++;
                    }
                }
                writeLogs(count, "listing directories", writer);
            }

            if(isDiffDictEnabled) {
                count = 0;
                List<String> directoriesList = (List<String>) shScriptMap.get("directories");
                for (String directories : directoriesList) {
                    String[] directory = directories.split(",");
                    Boolean isExecuted = execute("diff "+directory[0]+" "+directory[1]);
                    if(!isExecuted) {
                        count++;
                    }
                }

                writeLogs(count, "diff directories", writer);
            }

            if(isDiffFileEnabled) {
                count = 0;
                List<String> fileList = (List<String>) shScriptMap.get("files");
                for (String files : fileList) {
                    String[] file = files.split(",");
                    Boolean isExecuted = execute("diff "+file[0]+" "+file[1]);
                    if(!isExecuted) {
                        count++;
                    }
                }

                writeLogs(count, "diff files", writer);
            }

            if(isChecksumEnabled) {
                count = 0;
                List<String> fileList = (List<String>) shScriptMap.get("checksums");
                for (String files : fileList) {
                    String[] file = files.split(",");
                    Boolean isExecuted = execute("checksum "+file[0]+" "+file[1]);
                    if(!isExecuted) {
                        count++;
                    }
                }
                writeLogs(count, "checksum files", writer);
            }


            if(isProcessEnabled) {
                count = 0;
                List<String> processes = (List<String>) shScriptMap.get("processes");
                for(String process: processes) {
                    Boolean isExecuted = execute("ps -ef "+process);
                    if(!isExecuted) {
                        count++;
                    }
                }
                writeLogs(count, "processes", writer);
            }

            if(isSshEnabled) {
                count = 0;
                List<String> ssh = (List<String>) shScriptMap.get("ssh");
                for(String item: ssh) {
                    Boolean isExecuted = execute("sshg3 -B "+item+ " echo SSH Connection Success");
                    if(!isExecuted) {
                        count++;
                    }
                }
                writeLogs(count, "ssh connectivity", writer);
            }


        }
    }


    private static boolean containsForbiddenCommand(String input, List<String> forbiddenList) {
        for (String forbidden : forbiddenList) {
            // You can make this more strict with regex or word boundaries if needed
            if (input.matches(".*\\b" + Pattern.quote(forbidden) + "\\b.*")) {
                return true;
            }
        }
        return false;
    }


    private Boolean execute(String command) {
        try {
            if(!containsForbiddenCommand(command, restrictedCommands)) {
                builder.command("bash", "-c", command);
                builder.redirectOutput(ProcessBuilder.Redirect.appendTo(log));
                builder.redirectError(ProcessBuilder.Redirect.appendTo(log));
                Process process = builder.start();
                process.getInputStream().transferTo(System.out);
                process.getErrorStream().transferTo(System.err);
                int exitCode = process.waitFor();
                System.out.println("Exit code: " + exitCode);
                return true;

            } else {
                System.out.println("Unable to execute the command as it is containing restricted command " +command);
                return false;
            }
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }


    }


    private void writeLogs(int count, String message, PrintWriter writer) {
        if(count >0) {
            writer.println("ERROR: "+ count + " issues reported in executing commands");
        } else {
            writer.println("No issue in executing commands");
        }
    }
}
