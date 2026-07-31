# CleanDrive+

CleanDrive+ is a Java-based console application for scanning directories, identifying duplicate files, showing the largest files, visualizing folder structure, and providing safe cleanup recommendations.

## Features

- Scan a directory recursively using DFS
- Build a folder graph hierarchy for directory visualization
- Detect duplicate files by SHA-256 hash using an AVL tree
- Prioritize largest files using a max heap
- Generate safe cleanup recommendations for duplicate files
- Delete selected files manually from the console

## Project Structure

- `src/com/cleandrive/Main.java` - application entry point
- `src/com/cleandrive/ui/ConsoleMenu.java` - console menu and user interaction
- `src/com/cleandrive/service/DirectoryScanner.java` - directory traversal and file indexing
- `src/com/cleandrive/service/StorageOptimizer.java` - deletion and cleanup recommendation logic
- `src/com/cleandrive/service/HashGenerator.java` - file hash generation utility
- `src/com/cleandrive/datastructures/AVLTree.java` - duplicate file grouping via AVL tree
- `src/com/cleandrive/datastructures/MaxHeap.java` - largest file prioritization
- `src/com/cleandrive/datastructures/DirectoryGraph.java` - folder graph hierarchy representation
- `src/com/cleandrive/model/FileRecord.java` - file metadata model
- `src/com/cleandrive/model/FolderNode.java` - folder node model

## Requirements

- Java 8 or higher

## Build & Run

From the project root (`c:\PDSA\clean-drive`):

```bash
javac -d out src/com/cleandrive/Main.java src/com/cleandrive/ui/ConsoleMenu.java src/com/cleandrive/service/*.java src/com/cleandrive/datastructures/*.java src/com/cleandrive/model/*.java
java -cp out com.cleandrive.Main
```

If you are using an IDE such as IntelliJ IDEA, import the project as a Java module and run `com.cleandrive.Main`.

## Usage

1. Choose option `1` to scan a directory by entering its absolute path.
2. Use option `2` to view duplicate file groups.
3. Use option `3` to view the top largest files.
4. Use option `4` to print the folder structure hierarchy.
5. Use option `5` to show safe cleanup recommendations for duplicates.
6. Use option `6` to delete a file manually.
7. Choose option `7` to exit.

## Notes

- The scan operation computes SHA-256 hashes for file contents, so scanning large directories may take time.
- The duplicate detection is based on file hashes.
- Manual deletion requires entering the exact file path and confirmation.
