package com.example.shellscript.components;

import com.example.shellscript.config.CommandsProp;
import com.example.shellscript.config.Config;
import com.example.shellscript.utils.AppUtils;
import com.example.shellscript.utils.LoggerUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static com.example.shellscript.utils.AppUtils.writeLogs;

@Component
public class CommandConfig {

    @Autowired
    AppUtils appUtils;

    @Value("${configDirectory}")
    private String configDirectory;

    @Value("${releaseCode}")
    private String releaseCode;


    private final ProcessBuilder builder = new ProcessBuilder();

    Yaml yaml = new Yaml();

    final static List<String> listOfRestrictedCommands = Arrays.asList("rm", "mkdir", "cp", "mv");

    @Autowired
    private Config systemConfig;

    File log;


    public void processLinuxCommands(File log) throws Exception {
        this.log = log;
        if(systemConfig.getIsCommandsEnabled()) {
            String path = String.format("classpath:%s/config_%s_commands.yaml", configDirectory, releaseCode);
            if(appUtils.getFile(path) != null) {
                CommandsProp commandsProp = systemConfig.getCommands();
                Map<String, Object> yamlData = yaml.load(appUtils.getFile(path).getInputStream());
                Map<String, Object> shScriptMap = (Map<String, Object>) yamlData.get("sh-commands");
                LoggerUtility.logMessage("Health Check Status update summary:\n");
                LoggerUtility.logMessage("======================================================================:\n");
                LoggerUtility.logMessage("Linux File Systems executing Commands:\n");
                int count = 0;

                int totalCount = 0;


                if(commandsProp.getIsCommandEnabled()) {
                    List<String> commands = (List<String>) shScriptMap.get("commands");
                    for (String command : commands) {
                        Boolean isExecuted = execute(command);
                        if(!isExecuted) {
                            count++;
                            totalCount++;
                        }
                    }
                    writeLogs(count, "executing commands");
                }
                LoggerUtility.logMessage("Linux File Systems listing directories:\n");
                if(commandsProp.getIsListingEnabled()) {
                    count = 0;
                    List<String> listings = (List<String>) shScriptMap.get("listings");
                    for (String directory: listings) {
                        Boolean isExecuted = execute("ls -ltr "+directory);
                        if(!isExecuted) {
                            count++;
                            totalCount++;
                        }
                    }
                    writeLogs(count, "listing directories");
                }

                LoggerUtility.logMessage("Linux File Systems diff directories:\n");

                if(commandsProp.getIsDiffDirectoryEnabled()) {
                    count = 0;
                    List<String> directoriesList = (List<String>) shScriptMap.get("directories");
                    for (String directories : directoriesList) {
                        String[] directory = directories.split(",");
                        Boolean isExecuted = execute("diff "+directory[0]+" "+directory[1]);
                        if(!isExecuted) {
                            count++;
                            totalCount++;
                        }
                    }

                    writeLogs(count, "diff directories");
                }

                LoggerUtility.logMessage("Linux File Systems diff files:\n");

                if(commandsProp.getIsDiffFilesEnabled()) {
                    count = 0;
                    List<String> fileList = (List<String>) shScriptMap.get("files");
                    for (String files : fileList) {
                        String[] file = files.split(",");
                        Boolean isExecuted = execute("diff "+file[0]+" "+file[1]);
                        if(!isExecuted) {
                            count++;
                            totalCount++;
                        }
                    }

                    writeLogs(count, "diff files");
                }
                LoggerUtility.logMessage("Linux File Systems checksum files:\n");
                if(commandsProp.getIsChecksumEnabled()) {
                    count = 0;
                    List<String> fileList = (List<String>) shScriptMap.get("checksums");
                    for (String files : fileList) {
                        String[] file = files.split(",");
                        Boolean isExecuted = execute("checksum "+file[0]+" "+file[1]);
                        if(!isExecuted) {
                            count++;
                            totalCount++;
                        }
                    }
                    writeLogs(count, "checksum files");
                }

                LoggerUtility.logMessage("Linux processes running status:\n");
                if(commandsProp.getIsProcessesEnabled()) {
                    count = 0;
                    List<String> processes = (List<String>) shScriptMap.get("processes");
                    for(String process: processes) {
                        Boolean isExecuted = execute("ps -ef | grep \" "+process+ "\"");
                        if(!isExecuted) {
                            count++;
                            totalCount++;
                        }
                    }
                    writeLogs(count, "processes");
                }
                LoggerUtility.logMessage("SSH Connectivity Status check:\n");
                if(commandsProp.getIsSshEnabled()) {
                    count = 0;
                    List<String> ssh = (List<String>) shScriptMap.get("ssh");
                    for(String item: ssh) {
                        Boolean isExecuted = execute("sshg3 -B "+item+ " echo SSH Connection Success");
                        if(!isExecuted) {
                            count++;totalCount++;
                        }
                    }
                    writeLogs(count, "ssh connectivity");
                }

                LoggerUtility.logMessage("Command Script execution failed with "+ totalCount +" errors. please check detailed logs for further information");

            }
        }

    }


    private static boolean containsForbiddenCommand(String input) {
        for (String forbidden : listOfRestrictedCommands) {
            // You can make this more strict with regex or word boundaries if needed
            if (input.matches(".*\\b" + Pattern.quote(forbidden) + "\\b.*")) {
                return true;
            }
        }
        return false;
    }

    private Boolean execute(String command) {
        try {
            if(!containsForbiddenCommand(command)) {
                System.out.println("Running Command " + command + "\n");
                builder.command("bash", "-c", command);
                builder.redirectOutput(ProcessBuilder.Redirect.appendTo(log));
                builder.redirectError(ProcessBuilder.Redirect.appendTo(log));
                Process process = builder.start();
                process.getInputStream().transferTo(System.out);
                process.getErrorStream().transferTo(System.err);
                int exitCode = process.waitFor();
                System.out.println("Exit code: " + exitCode);
                return exitCode != 0;
            } else {
                System.out.println("Unable to execute the command as it is containing restricted command " +command);
                return false;
            }
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }


    }
}
