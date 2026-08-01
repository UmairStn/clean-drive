package com.cleandrive.datastructures;

import com.cleandrive.model.FileRecord;
import com.cleandrive.model.FolderNode;

import java.util.List;

public class DirectoryGraph {
    private FolderNode root;

    public DirectoryGraph(String rootPath) {
        this.root = new FolderNode(rootPath);
    }

    public FolderNode getRoot() {
        return root;
    }

    public void printGraphStructure() {
        System.out.println("\n--- Folder Structure Hierarchy (Graph Visualization) ---");
        printRecursive(root, "");
    }

    private void printRecursive(FolderNode node, String indent) {
        System.out.println(indent + "├── [Folder] " + node.getFolderPath());
        for (FolderNode child : node.getSubfolders()) {
            printRecursive(child, indent + "│   ");
        }
    }

    // --- NOVEL FEATURE: Print Graph Clutter Heatmap ---

    public void printClutterHeatmap(List<List<FileRecord>> duplicateGroups) {
        System.out.println("\n=================================================");
        System.out.println("   FOLDER CLUTTER HEATMAP SCORE (DFS Graph Walk) ");
        System.out.println("=================================================");
        printClutterRecursive(root, "", duplicateGroups);
    }

    private void printClutterRecursive(FolderNode node, String indent, List<List<FileRecord>> duplicateGroups) {
        double score = node.getClutterScore(duplicateGroups);
        long totalMB = node.getTotalBytes() / (1024 * 1024);
        long wastedMB = node.getWastedBytes(duplicateGroups) / (1024 * 1024);

        String tag;
        if (score >= 50.0) {
            tag = "[CRITICAL CLUTTER]";
        } else if (score >= 20.0) {
            tag = "[MODERATE CLUTTER]";
        } else {
            tag = "[CLEAN]";
        }

        System.out.printf("%s├── %s %s | Score: %.1f%% (Wasted: %d MB / Total: %d MB)\n",
                indent, tag, node.getFolderPath(), score, wastedMB, totalMB);

        for (FolderNode child : node.getSubfolders()) {
            printClutterRecursive(child, indent + "│   ", duplicateGroups);
        }
    }
}