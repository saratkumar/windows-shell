package com.example.shellscript.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@ConfigurationProperties(prefix = "flags")
public class Config {
    private CommandsProp commands;

    private Boolean isCommandsEnabled;

    public Boolean getIsCommandsEnabled() {
        return isCommandsEnabled;
    }

    public void setIsCommandsEnabled(Boolean commandsEnabled) {
        isCommandsEnabled = commandsEnabled;
    }

    public CommandsProp getCommands() {
        return commands;
    }

    public void setCommands(CommandsProp commands) {
        this.commands = commands;
    }

}



