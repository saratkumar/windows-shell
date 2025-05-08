package com.example.shellscript.config;

public class CommandsProp {
    private Boolean isCommandEnabled;

    private Boolean isDiffDirectoryEnabled;

    private Boolean isDiffFilesEnabled;

    private Boolean isListingEnabled;

    private Boolean isChecksumEnabled;

    private Boolean isProcessesEnabled;

    private Boolean isSshEnabled;

    public Boolean getIsCommandEnabled() {
        return isCommandEnabled;
    }

    public Boolean getIsDiffDirectoryEnabled() {
        return isDiffDirectoryEnabled;
    }

    public Boolean getIsDiffFilesEnabled() {
        return isDiffFilesEnabled;
    }

    public Boolean getIsListingEnabled() {
        return isListingEnabled;
    }

    public Boolean getIsChecksumEnabled() {
        return isChecksumEnabled;
    }

    public Boolean getIsProcessesEnabled() {
        return isProcessesEnabled;
    }

    public Boolean getIsSshEnabled() {
        return isSshEnabled;
    }

    public void setIsCommandEnabled(Boolean commandEnabled) {
        isCommandEnabled = commandEnabled;
    }

    public void setIsDiffDirectoryEnabled(Boolean diffDirectoryEnabled) {
        isDiffDirectoryEnabled = diffDirectoryEnabled;
    }

    public void setIsDiffFilesEnabled(Boolean diffFilesEnabled) {
        isDiffFilesEnabled = diffFilesEnabled;
    }

    public void setIsListingEnabled(Boolean listingEnabled) {
        isListingEnabled = listingEnabled;
    }

    public void setIsChecksumEnabled(Boolean checksumEnabled) {
        isChecksumEnabled = checksumEnabled;
    }

    public void setIsProcessesEnabled(Boolean processesEnabled) {
        isProcessesEnabled = processesEnabled;
    }

    public void setIsSshEnabled(Boolean sshEnabled) {
        isSshEnabled = sshEnabled;
    }
}