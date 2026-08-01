package com.cleandrive.service;

import com.cleandrive.model.FileRecord;
import java.io.File;
import java.util.List;

public class StorageOptimizer {

    public static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            return file.delete();
        }
        return false;
    }

    public static void generateRecommendations(List<List<FileRecord>> duplicateGroups) {
        if (duplicateGroups.isEmpty()) {
            System.out.println("No duplicates found to optimize.");
            return;
        }

        System.out.println("\n--- Safe Cleanup Recommendations ---");
        long totalSavings = 0;

        for (int i = 0; i < duplicateGroups.size(); i++) {
            List<FileRecord> group = duplicateGroups.get(i);
            System.out.println("\nDuplicate Group " + (i + 1) + " [Hash: " + group.get(0).getFileHash() + "]:");

            FileRecord keepFile = group.get(0);
            for (FileRecord f : group) {
                if (f.getLastModified().isBefore(keepFile.getLastModified())) {
                    keepFile = f;
                }
            }

            System.out.println("  [RECOMMENDED KEEP] " + keepFile.getFilePath() + " (Oldest creation date)");

            for (FileRecord f : group) {
                if (!f.getFilePath().equals(keepFile.getFilePath())) {
                    System.out.println("  [SAFE TO DELETE]   " + f.getFilePath());
                    totalSavings += f.getSizeInBytes();
                }
            }
        }

        double mbSavings = totalSavings / (1024.0 * 1024.0);
        System.out.printf("\nPotential Reclaimable Disk Space: %.2f MB\n", mbSavings);
    }

    public static int autoClean(List<List<FileRecord>> duplicateGroups) {
        int deletedCount = 0;
        if (duplicateGroups.isEmpty()) {
            System.out.println("No duplicates found to clean.");
            return deletedCount;
        }

        for (int i = 0; i < duplicateGroups.size(); i++) {
            List<FileRecord> group = duplicateGroups.get(i);
            
            FileRecord keepFile = group.get(0);
            for (FileRecord f : group) {
                if (f.getLastModified().isBefore(keepFile.getLastModified())) {
                    keepFile = f;
                }
            }

            for (FileRecord f : group) {
                if (!f.getFilePath().equals(keepFile.getFilePath())) {
                    if (deleteFile(f.getFilePath())) {
                        deletedCount++;
                    }
                }
            }
        }
        return deletedCount;
    }
}