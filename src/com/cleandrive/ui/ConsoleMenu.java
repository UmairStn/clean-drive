package com.cleandrive.ui;

import com.cleandrive.datastructures.AVLTree;
import com.cleandrive.datastructures.DirectoryGraph;
import com.cleandrive.datastructures.MaxHeap;
import com.cleandrive.model.FileRecord;
import com.cleandrive.service.DirectoryScanner;
import com.cleandrive.service.StorageOptimizer;

import javax.swing.JFileChooser;
import javax.swing.UIManager;
import java.io.File;
import java.util.List;
import java.util.Scanner;

public class ConsoleMenu {

    private AVLTree avlTree = new AVLTree();
    private MaxHeap maxHeap = new MaxHeap();
    private DirectoryScanner scannerService = new DirectoryScanner();
    private DirectoryGraph activeGraph = null;
    private String activePath = null;
    private Scanner scanner = new Scanner(System.in);

    public ConsoleMenu() {
        // Set native system look and feel for the file picker
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
    }

    public void start() {
        System.out.println("=================================================");
        System.out.println("             CleanDrive+ Storage Optimizer       ");
        System.out.println("=================================================");

        while (true) {
            // STEP 1: Get directory path using 3 initial options
            if (activePath == null) {
                boolean proceed = promptForInitialPath();
                if (!proceed || activePath == null) {
                    return; // Exit application
                }
            }

            // STEP 2: Display main menu with active path pinned
            displayMenu();

            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("exit")) {
                System.out.println("Exiting CleanDrive+. Goodbye!");
                return;
            }

            if (input.equalsIgnoreCase("clear path") || input.equalsIgnoreCase("change path")) {
                clearActivePath();
                continue;
            }

            int choice = parseChoice(input);

            switch (choice) {
                case 1:
                    executeScan(activePath);
                    break;
                case 2:
                    handleDuplicates();
                    break;
                case 3:
                    handleLargestFiles();
                    break;
                case 4:
                    handleGraphView();
                    break;
                case 5:
                    StorageOptimizer.generateRecommendations(avlTree.getDuplicateGroups());
                    break;
                case 6:
                    handleDelete();
                    break;
                case 7:
                    clearActivePath();
                    break;
                case 8:
                    System.out.println("Exiting CleanDrive+. Goodbye!");
                    return;
                default:
                    System.out.println("Invalid option. Enter 1-8 or type 'clear path' / 'exit'.");
            }
        }
    }

    private boolean promptForInitialPath() {
        while (activePath == null) {
            System.out.println("\n-------------------------------------------------");
            System.out.println("Select Path Input Method:");
            System.out.println("1. Open File Picker");
            System.out.println("2. Enter Direct Path");
            System.out.println("3. Exit");
            System.out.print("Select an option (1-3): ");

            String choiceStr = scanner.nextLine().trim();
            int choice = parseChoice(choiceStr);

            switch (choice) {
                case 1:
                    openFilePicker();
                    break;
                case 2:
                    enterPathManually();
                    break;
                case 3:
                    System.out.println("Exiting CleanDrive+. Goodbye!");
                    return false;
                default:
                    System.out.println("Invalid selection. Please choose 1, 2, or 3.");
            }
        }
        return true;
    }

    private void openFilePicker() {
        System.out.println("Opening Windows File Picker dialog...");

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select Folder to Scan - CleanDrive+");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);

        int result = chooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFolder = chooser.getSelectedFile();
            activePath = selectedFolder.getAbsolutePath();
            System.out.println("Selected Directory: " + activePath);
            executeScan(activePath);
        } else {
            System.out.println("No folder selected from File Picker.");
        }
    }

    private void enterPathManually() {
        System.out.print("Enter absolute directory path: ");
        String pathInput = scanner.nextLine().trim();

        File dir = new File(pathInput);
        if (dir.exists() && dir.isDirectory()) {
            activePath = dir.getAbsolutePath();
            executeScan(activePath);
        } else {
            System.out.println("Error: Invalid directory path! Path does not exist or is not a folder.");
        }
    }

    private void displayMenu() {
        System.out.println("\n=================================================");
        System.out.println(" ACTIVE PATH: " + activePath);
        System.out.println("=================================================");
        System.out.println("1. Rescan Current Directory");
        System.out.println("2. View Duplicate Files (AVL Tree)");
        System.out.println("3. View Top Largest Files (Max Heap)");
        System.out.println("4. View Folder Graph Hierarchy");
        System.out.println("5. Safe Cleanup Recommendations");
        System.out.println("6. Delete File Manually");
        System.out.println("7. Clear / Change Path");
        System.out.println("8. Exit");
        System.out.print("Select an option (1-8): ");
    }

    private void executeScan(String path) {
        System.out.println("\nScanning directory and processing data structures...");
        DirectoryGraph resultGraph = scannerService.scanDirectoryDFS(path, avlTree, maxHeap);

        if (resultGraph == null) {
            System.out.println("Error: Failed to scan directory path.");
            activePath = null;
            return;
        }

        activeGraph = resultGraph;
        System.out.println("Scanning completed in path (" + path + ")");
    }

    private void clearActivePath() {
        activePath = null;
        activeGraph = null;
        avlTree.clear();
        maxHeap.clear();
        System.out.println("\nActive path cleared successfully!");
    }

    private void handleDuplicates() {
        List<List<FileRecord>> duplicates = avlTree.getDuplicateGroups();
        if (duplicates.isEmpty()) {
            System.out.println("\nNo duplicate files found in current path.");
            return;
        }

        System.out.println("\n--- Duplicate File Groups Found (AVL Tree Indexing) ---");
        for (int i = 0; i < duplicates.size(); i++) {
            System.out.println("\nGroup " + (i + 1) + ":");
            for (FileRecord file : duplicates.get(i)) {
                System.out.println("  -> " + file.getFilePath());
            }
        }
    }

    private void handleLargestFiles() {
        if (maxHeap.isEmpty()) {
            System.out.println("\nNo files scanned yet.");
            return;
        }

        System.out.print("Enter number of top largest files to view: ");
        int topN = getIntInput();

        MaxHeap tempHeap = maxHeap.cloneHeap();
        System.out.println("\n--- Top " + topN + " Largest Files (Max Heap Prioritization) ---");
        int count = 0;

        while (!tempHeap.isEmpty() && count < topN) {
            FileRecord maxFile = tempHeap.extractMax();
            System.out.println((count + 1) + ". " + maxFile);
            count++;
        }
    }

    private void handleGraphView() {
        if (activeGraph == null) {
            System.out.println("\nPlease run a directory scan first.");
            return;
        }
        activeGraph.printGraphStructure();
    }

    private void handleDelete() {
        System.out.print("Enter full path of file to delete: ");
        String filePath = scanner.nextLine().trim();

        System.out.print("Are you sure you want to permanently delete this file? (yes/no): ");
        String confirm = scanner.nextLine().trim();

        if (confirm.equalsIgnoreCase("yes")) {
            boolean deleted = StorageOptimizer.deleteFile(filePath);
            if (deleted) {
                System.out.println("File deleted successfully!");
            } else {
                System.out.println("Failed to delete file. Check path permissions.");
            }
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    private int parseChoice(String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private int getIntInput() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}