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

    // ANSI Escape Codes for UI Styling
    public static final String RESET = "\u001B[0m";
    public static final String CYAN = "\u001B[36m";   // Primary neon accent
    public static final String GREEN = "\u001B[32m";  // Success state
    public static final String RED = "\u001B[31m";    // Warning/Exit state
    public static final String BOLD = "\u001B[1m";    // Emphasized text

    private AVLTree avlTree = new AVLTree();
    private MaxHeap maxHeap = new MaxHeap();
    private DirectoryScanner scannerService = new DirectoryScanner();
    private DirectoryGraph activeGraph = null;
    private String activePath = null;
    private Scanner scanner = new Scanner(System.in);

    public ConsoleMenu() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
    }

    public void start() {
        System.out.println(CYAN + BOLD + "\n=================================================" + RESET);
        System.out.println(CYAN + BOLD + "           CleanDrive+ Storage Optimizer         " + RESET);
        System.out.println(CYAN + BOLD + "=================================================" + RESET);

        while (true) {
            // STEP 1: Get directory path using initial prompt
            if (activePath == null) {
                boolean proceed = promptForInitialPath();
                if (!proceed || activePath == null) {
                    return; // Exit application cleanly
                }
            }

            // STEP 2: Display main menu with active path pinned
            displayMenu();

            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("exit")) {
                System.out.println(RED + BOLD + "Exiting CleanDrive+. System offline." + RESET);
                return;
            }

            if (input.equalsIgnoreCase("clear path") || input.equalsIgnoreCase("change path")) {
                clearActivePath();
                continue;
            }

            int choice = parseChoice(input);

            // MAIN MENU SWITCH
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
                    handleClutterHeatmap();
                    break;
                case 6:
                    StorageOptimizer.generateRecommendations(avlTree.getDuplicateGroups());
                    break;
                case 7:
                    handleDelete();
                    break;
                case 8:
                    clearActivePath();
                    break;
                case 9:
                    System.out.println(RED + BOLD + "Exiting CleanDrive+. System offline." + RESET);
                    return;
                default:
                    System.out.println(RED + " [!] Invalid option. Please select an option from 1 to 9." + RESET);
            }
        }
    }

    // INITIAL PATH METHOD
    private boolean promptForInitialPath() {
        while (activePath == null) {
            System.out.println(CYAN + "\n-------------------------------------------------" + RESET);
            System.out.println(BOLD + "Select Path Input Method:" + RESET);
            System.out.println(BOLD + "  [1]" + RESET + " Open File Picker");
            System.out.println(BOLD + "  [2]" + RESET + " Enter Direct Path");
            System.out.println(BOLD + "  [3]" + RESET + RED + " Exit" + RESET);
            System.out.print(BOLD + "  > Select an option (1-3): " + RESET);

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
                    System.out.println(RED + BOLD + "Exiting CleanDrive+. System offline." + RESET);
                    return false;
                default:
                    System.out.println(RED + " [!] Invalid selection. Please choose 1, 2, or 3." + RESET);
            }
        }
        return true;
    }

    private void openFilePicker() {
        System.out.println(CYAN + "Opening Windows File Picker dialog..." + RESET);

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select Folder to Scan - CleanDrive+");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);

        int result = chooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFolder = chooser.getSelectedFile();
            activePath = selectedFolder.getAbsolutePath();
            System.out.println(GREEN + "Selected Directory: " + activePath + RESET);
            executeScan(activePath);
        } else {
            System.out.println(RED + "No folder selected from File Picker." + RESET);
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
            System.out.println(RED + BOLD + " [!] Error: Invalid directory path! Path does not exist or is not a folder." + RESET);
        }
    }

    private void displayMenu() {
        System.out.println(CYAN + BOLD + "\n=================================================" + RESET);
        System.out.println(CYAN + BOLD + "   [ C L E A N D R I V E + ]   System Active" + RESET);
        System.out.println(CYAN + BOLD + "=================================================" + RESET);
        System.out.println(GREEN + " ACTIVE PATH: " + activePath + RESET);
        System.out.println(CYAN + "-------------------------------------------------" + RESET);
        System.out.println(BOLD + "  [1]" + RESET + " Rescan Current Directory");
        System.out.println(BOLD + "  [2]" + RESET + " View Duplicate Files (AVL Tree)");
        System.out.println(BOLD + "  [3]" + RESET + " View Top Largest Files (Max Heap)");
        System.out.println(BOLD + "  [4]" + RESET + " View Folder Graph Hierarchy");
        System.out.println(BOLD + "  [5]" + RESET + " View Folder Clutter Heatmap");
        System.out.println(BOLD + "  [6]" + RESET + " Safe Cleanup Recommendations");
        System.out.println(BOLD + "  [7]" + RESET + " Delete File Manually");
        System.out.println(BOLD + "  [8]" + RESET + " Clear / Change Path");
        System.out.println(BOLD + "  [9]" + RESET + RED + " Exit Application" + RESET);
        System.out.println(CYAN + "-------------------------------------------------" + RESET);
        System.out.print(BOLD + "  > SELECT AN OPTION (1-9): " + RESET);
    }

    private void executeScan(String path) {
        System.out.println(CYAN + "\nScanning directory and processing data structures..." + RESET);
        DirectoryGraph resultGraph = scannerService.scanDirectoryDFS(path, avlTree, maxHeap);

        if (resultGraph == null) {
            System.out.println(RED + BOLD + " [!] Error: Failed to scan directory path." + RESET);
            activePath = null;
            return;
        }

        activeGraph = resultGraph;
        System.out.println(GREEN + " [✓] Scanning completed in path (" + path + ")" + RESET);
    }

    private void clearActivePath() {
        activePath = null;
        activeGraph = null;
        avlTree.clear();
        maxHeap.clear();
        System.out.println(GREEN + "\n [✓] Active path cleared successfully!" + RESET);
    }

    private void handleDuplicates() {
        List<List<FileRecord>> duplicates = avlTree.getDuplicateGroups();
        if (duplicates.isEmpty()) {
            System.out.println(GREEN + "\n [✓] No duplicate files found in current path." + RESET);
            return;
        }

        System.out.println(CYAN + BOLD + "\n--- Duplicate File Groups Found (AVL Tree Indexing) ---" + RESET);
        for (int i = 0; i < duplicates.size(); i++) {
            System.out.println(BOLD + "\nGroup " + (i + 1) + ":" + RESET);
            for (FileRecord file : duplicates.get(i)) {
                System.out.println("  -> " + file.getFilePath());
            }
        }
    }

    private void handleLargestFiles() {
        if (maxHeap.isEmpty()) {
            System.out.println(RED + "\n [!] No files scanned yet." + RESET);
            return;
        }

        System.out.print(CYAN + "Enter number of top largest files to view: " + RESET);
        int topN = getIntInput();

        MaxHeap tempHeap = maxHeap.cloneHeap();
        System.out.println(CYAN + BOLD + "\n--- Top " + topN + " Largest Files (Max Heap Prioritization) ---" + RESET);
        int count = 0;

        while (!tempHeap.isEmpty() && count < topN) {
            FileRecord maxFile = tempHeap.extractMax();
            System.out.println((count + 1) + ". " + maxFile);
            count++;
        }
    }

    private void handleGraphView() {
        if (activeGraph == null) {
            System.out.println(RED + "\n [!] Please run a directory scan first." + RESET);
            return;
        }
        activeGraph.printGraphStructure();
    }

    private void handleClutterHeatmap() {
        if (activeGraph == null) {
            System.out.println(RED + "\n [!] Please run a directory scan first." + RESET);
            return;
        }
        activeGraph.printClutterHeatmap(avlTree.getDuplicateGroups());
    }

    private void handleDelete() {
        System.out.println(CYAN + "\n-------------------------------------------------" + RESET);
        System.out.println(BOLD + "Select File Deletion Method:" + RESET);
        System.out.println(BOLD + "  [1]" + RESET + " Select File using File Picker");
        System.out.println(BOLD + "  [2]" + RESET + " Enter Full File Path Manually");
        System.out.println(BOLD + "  [3]" + RESET + " Cancel");
        System.out.print(BOLD + "  > Select an option (1-3): " + RESET);

        String choiceStr = scanner.nextLine().trim();
        int choice = parseChoice(choiceStr);

        String filePath = null;

        switch (choice) {
            case 1:
                filePath = openSingleFilePicker();
                break;
            case 2:
                System.out.print("Enter full path of file to delete: ");
                filePath = scanner.nextLine().trim();
                break;
            case 3:
                System.out.println("Deletion cancelled.");
                return;
            default:
                System.out.println(RED + " [!] Invalid selection. Deletion cancelled." + RESET);
                return;
        }

        if (filePath == null || filePath.isEmpty()) {
            System.out.println(RED + "No file selected." + RESET);
            return;
        }

        File targetFile = new File(filePath);
        if (!targetFile.exists() || !targetFile.isFile()) {
            System.out.println(RED + BOLD + " [!] Error: Specified path is invalid or is not a file." + RESET);
            return;
        }

        System.out.println(CYAN + "\nSelected File: " + targetFile.getAbsolutePath() + RESET);
        System.out.print(RED + BOLD + "Are you sure you want to permanently delete this file? (yes/no): " + RESET);
        String confirm = scanner.nextLine().trim();

        if (confirm.equalsIgnoreCase("yes")) {
            boolean deleted = StorageOptimizer.deleteFile(targetFile.getAbsolutePath());
            if (deleted) {
                System.out.println(GREEN + BOLD + " [✓] File deleted successfully!" + RESET);
            }
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    private String openSingleFilePicker() {
        System.out.println(CYAN + "Opening Windows File Picker dialog..." + RESET);

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select File to Delete - CleanDrive+");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setAcceptAllFileFilterUsed(true);

        // Pre-set directory to active path if available
        if (activePath != null) {
            chooser.setCurrentDirectory(new File(activePath));
        }

        int result = chooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile().getAbsolutePath();
        }
        return null;
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