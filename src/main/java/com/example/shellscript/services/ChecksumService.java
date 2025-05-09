package com.example.shellscript.services;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

@Service
public class ChecksumService {



    public int checksumComparison(String path1, String path2) {
        int count = 0;
        try {
            Map<String, String> folder1Checksums = getChecksumsForFolder(path1);
            Map<String, String> folder2Checksums = getChecksumsForFolder(path2);

            count = compareChecksums(folder1Checksums, folder2Checksums);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return count > 0 ? 1 : 0  ;
    }


    // Get checksums for all files in a folder
    public static Map<String, String> getChecksumsForFolder(String folderPath) throws Exception {
        Map<String, String> fileChecksums = new HashMap<>();

        // Command to list all files in the folder (Recursively) based on OS
        ProcessBuilder pb;
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) {
            // Windows: Use `dir /b /s` to list files recursively
            pb = new ProcessBuilder("cmd.exe", "/c", "dir", "/b", "/s", folderPath);
        } else {
            // Linux/Mac: Use `find` to list files recursively
            pb = new ProcessBuilder("find", folderPath, "-type", "f");
        }

        Process process = pb.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        String filePath;
        while ((filePath = reader.readLine()) != null) {
            // Run checksum command on each file
            String checksum = getFileChecksum(filePath);
            fileChecksums.put(filePath, checksum);
        }

        process.waitFor();
        return fileChecksums;
    }

    // Get file checksum using platform-specific command
    public static String getFileChecksum(String filePath) throws IOException, InterruptedException, IOException {
        String os = System.getProperty("os.name").toLowerCase();
        String checksum = "";

        if (os.contains("win")) {
            // Windows: Use certutil to compute SHA-256 checksum
            ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", "certutil", "-hashfile", filePath, "SHA256");
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().matches("[a-fA-F0-9]{64}")) {
                    checksum = line.trim();
                    break;
                }
            }

            process.waitFor();
        } else {
            // Linux/Mac: Use sha256sum
            ProcessBuilder pb = new ProcessBuilder("sha256sum", filePath);
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line = reader.readLine();
            if (line != null) {
                checksum = line.split(" ")[0]; // Extract checksum from output
            }

            process.waitFor();
        }

        return checksum;
    }

    // Compare checksums of files between two folders
    public static int compareChecksums(Map<String, String> folder1Checksums, Map<String, String> folder2Checksums) {
        // Use the folder with fewer files as the "reference" to check missing files in the other folder
        Set<String> commonFiles = new HashSet<>(folder1Checksums.keySet());
        commonFiles.retainAll(folder2Checksums.keySet());
        int count = 0;
        boolean isSame = true;

        // Compare checksums of common files
        for (String filePath : commonFiles) {
            String checksum1 = folder1Checksums.get(filePath);
            String checksum2 = folder2Checksums.get(filePath);

            if (checksum1 == null || checksum2 == null || !checksum1.equals(checksum2)) {
                isSame = false;
                System.out.println("File mismatch: " + filePath);
                System.out.println("Folder1 checksum: " + checksum1);
                System.out.println("Folder2 checksum: " + checksum2);
            }
        }

        // Report missing files in Folder1
        for (String filePath : folder2Checksums.keySet()) {
            if (!folder1Checksums.containsKey(filePath)) {
                isSame = false;
                System.out.println("File missing in Folder1: " + filePath);
                count++;
            }
        }

        // Report missing files in Folder2
        for (String filePath : folder1Checksums.keySet()) {
            if (!folder2Checksums.containsKey(filePath)) {
                isSame = false;
                count++;
                System.out.println("File missing in Folder2: " + filePath);
            }
        }

        if (isSame) {
            System.out.println("The common files in both folders have identical contents.");
        } else {
            System.out.println("The folders have differences.");
        }

        return count;
    }
}
