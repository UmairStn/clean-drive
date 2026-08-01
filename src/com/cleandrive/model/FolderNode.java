package com.cleandrive.model;

import java.util.ArrayList;
import java.util.List;

public class FolderNode {
    private String folderPath;
    private List<FolderNode> subfolders;
    private List<FileRecord> files;

    public FolderNode(String folderPath) {
        this.folderPath = folderPath;
        this.subfolders = new ArrayList<>();
        this.files = new ArrayList<>();
    }

    public String getFolderPath() { return folderPath; }
    public List<FolderNode> getSubfolders() { return subfolders; }
    public List<FileRecord> getFiles() { return files; }

    public void addSubfolder(FolderNode child) {
        this.subfolders.add(child);
    }

    public void addFile(FileRecord file) {
        this.files.add(file);
    }

    // --- NOVEL FEATURE: Clutter Score Calculations ---

    /**
     * Calculates total bytes stored directly in this folder and all subfolders.
     */
    public long getTotalBytes() {
        long total = 0;
        for (FileRecord file : files) {
            total += file.getSizeInBytes();
        }
        for (FolderNode sub : subfolders) {
            total += sub.getTotalBytes();
        }
        return total;
    }

    /**
     * Calculates total wasted bytes from duplicate files in this folder and all subfolders.
     */
    public long getWastedBytes(List<List<FileRecord>> duplicateGroups) {
        long wasted = 0;

        // Check files in current folder
        for (FileRecord file : files) {
            if (isDuplicateFile(file, duplicateGroups)) {
                wasted += file.getSizeInBytes();
            }
        }

        // Recursively check subfolders
        for (FolderNode sub : subfolders) {
            wasted += sub.getWastedBytes(duplicateGroups);
        }

        return wasted;
    }

    /**
     * Clutter Score (%) = (Wasted Bytes / Total Bytes) * 100
     */
    public double getClutterScore(List<List<FileRecord>> duplicateGroups) {
        long total = getTotalBytes();
        if (total == 0) return 0.0;
        long wasted = getWastedBytes(duplicateGroups);
        return ((double) wasted / total) * 100.0;
    }

    private boolean isDuplicateFile(FileRecord file, List<List<FileRecord>> duplicateGroups) {
        for (List<FileRecord> group : duplicateGroups) {
            // Check if this file is in the duplicate group by matching file path
            boolean isInGroup = false;
            for (FileRecord f : group) {
                if (f.getFilePath().equals(file.getFilePath())) {
                    isInGroup = true;
                    break;
                }
            }

            if (isInGroup) {
                // Find which file is recommended to keep (oldest timestamp)
                FileRecord keepFile = group.get(0);
                for (FileRecord f : group) {
                    if (f.getLastModified().isBefore(keepFile.getLastModified())) {
                        keepFile = f;
                    }
                }
                // It is marked as wasted space if it is NOT the file to keep
                return !file.getFilePath().equals(keepFile.getFilePath());
            }
        }
        return false;
    }
}