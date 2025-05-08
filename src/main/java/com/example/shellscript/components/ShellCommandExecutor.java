package com.example.shellscript.components;

import com.example.shellscript.services.SqlService;
import com.example.shellscript.utils.LoggerUtility;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class ShellCommandExecutor {

    @Autowired
    private Environment env;

    @Autowired
    CommandConfig commandConfig;

    @Autowired
    SqlService sqlService;

    String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));

    String logFileName = "command_" + timestamp + ".log";

    String summaryLogFile = "summary_log_" + timestamp + ".log";

    File log = null;

    @PostConstruct
    public void readConditionalCommands() throws Exception {
        String appCode = env.getProperty("release.code");

        File logDir = new File("logs");
        if (!logDir.exists()) {
            logDir.mkdirs();
        }
        log = new File(logDir, logFileName);
        File summaryLog = new File(logDir, summaryLogFile);
        LoggerUtility.setupDualLogging(log, summaryLog, true);

        commandConfig.processLinuxCommands(log);

        sqlService.processQuery();


    }



}
