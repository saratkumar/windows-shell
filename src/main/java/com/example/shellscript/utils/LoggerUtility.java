package com.example.shellscript.utils;

import java.io.*;

public class LoggerUtility {

    private static PrintStream consoleStream;
    private static PrintWriter fileWriter;

    /**
     * Redirects System.out to both a log file and the console.
     */
    public static void setupDualLogging(File detailedLogFile, File summaryLogFile, boolean append) {
        try {
            // Create file writer to log into the log file
            fileWriter = new PrintWriter(new FileWriter(summaryLogFile, append));

            // Create print stream to redirect to console and file
            consoleStream = new PrintStream(new FileOutputStream(detailedLogFile, append));
//            System.setOut(consoleStream);  // Redirect System.out to the log file
//            System.setErr(consoleStream);  // Optionally, redirect System.err as well for error logs


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Log a message to the file and console.
     */
    public static void logMessage(String message) {
        // Output the message to console (via System.out)
        System.out.println(message);

        // Also log it to the file
        if (fileWriter != null) {
            fileWriter.println(message);
            fileWriter.flush();  // Ensure the message is written immediately to the file
        }
    }

    /**
     * Close the file writer (flushes and closes the file stream).
     */
    public static void closeFileWriter() {
        if (fileWriter != null) {
            fileWriter.close();
        }
    }
}
