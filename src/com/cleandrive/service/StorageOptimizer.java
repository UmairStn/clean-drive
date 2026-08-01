package com.cleandrive.service;

import com.cleandrive.model.FileRecord;
import java.io.File;
import java.util.List;

public class StorageOptimizer {

    public static boolean deleteFile(String filePath) {
        try {
            java.nio.file.Path path = java.nio.file.Paths.get(filePath);
            java.nio.file.Files.delete(path);
            return true;
        } catch (java.nio.file.NoSuchFileException e) {
            System.out.println("Error: File does not exist at specified path.");
        } catch (java.nio.file.AccessDeniedException e) {
            System.out.println("Error: Access denied. Run IDE as Administrator or check permissions.");
        } catch (java.io.IOException e) {
            System.out.println("Error: File is locked by another process (close open PDF viewers).");
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

            // Smart check: Pick original file over copy files
            FileRecord keepFile = group.get(0);
            for (FileRecord f : group) {
                if (isBetterToKeep(f, keepFile)) {
                    keepFile = f;
                }
            }

            System.out.println("  [RECOMMENDED KEEP] " + keepFile.getFilePath() + " (Original File)");

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

    private static boolean isBetterToKeep(FileRecord candidate, FileRecord currentBest) {
        String candName = candidate.getFileName().toLowerCase();
        String bestName = currentBest.getFileName().toLowerCase();

        boolean candIsCopy = candName.contains("copy") || candName.contains("(1)") || candName.contains("(2)");
        boolean bestIsCopy = bestName.contains("copy") || bestName.contains("(1)") || bestName.contains("(2)");

        // If current best is a copy and candidate is NOT a copy, prefer candidate
        if (bestIsCopy && !candIsCopy) {
            return true;
        }
        // If candidate is a copy and current best is NOT, keep current best
        if (candIsCopy && !bestIsCopy) {
            return false;
        }

        // Fallback: Pick older timestamp if both are normal or both are copies
        return candidate.getLastModified().isBefore(currentBest.getLastModified());
    }
}