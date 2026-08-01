# CleanDrive+

CleanDrive+ is a lightweight Java console utility to help find duplicate files, inspect the largest files, and generate safe cleanup recommendations to reclaim disk space.

**Key features**

- Recursive directory scanning (DFS) with file indexing
- Duplicate detection using SHA-256 hashes stored in an AVL tree
- Fast retrieval of largest files via a max-heap
- Folder hierarchy visualization through a directory graph
- Safe cleanup recommendations and optional manual deletion

## Table of Contents

- Overview
- Quick Start
- Project Structure
- Usage
- Notes & Limitations
- Contributing
- License

## Quick Start

Requirements: Java 8 or higher.

From the project root (`c:\PDSA\clean-drive`) compile and run:

```bash
javac -d out src/com/cleandrive/Main.java src/com/cleandrive/ui/ConsoleMenu.java \
	src/com/cleandrive/service/*.java src/com/cleandrive/datastructures/*.java src/com/cleandrive/model/*.java
java -cp out com.cleandrive.Main
```

Tips:

- For large scans, run the application on a subdirectory first to validate results.
- Run from an account with read access to the folders you want to scan.

## Project Structure

- `src/com/cleandrive/Main.java` — application entry point
- `src/com/cleandrive/ui/ConsoleMenu.java` — console UI and user input handling
- `src/com/cleandrive/service/DirectoryScanner.java` — directory traversal and indexing
- `src/com/cleandrive/service/StorageOptimizer.java` — recommendations and deletion helpers
- `src/com/cleandrive/service/HashGenerator.java` — SHA-256 file hashing utility
- `src/com/cleandrive/datastructures/AVLTree.java` — used to group files by hash
- `src/com/cleandrive/datastructures/MaxHeap.java` — tracks largest files
- `src/com/cleandrive/datastructures/DirectoryGraph.java` — folder graph representation
- `src/com/cleandrive/model/FileRecord.java` — file metadata model
- `src/com/cleandrive/model/FolderNode.java` — folder node model

## Usage

Run the program and choose from the console menu:

1. Scan a directory: enter the absolute path to begin indexing files recursively.
2. View duplicate groups: lists groups of files that share the same SHA-256 hash.
3. Show largest files: displays the top N largest files (sorted by size).
4. Print folder hierarchy: text-based visualization of folders and sizes.
5. Show cleanup recommendations: safe suggestions for duplicate removal (keeps one copy per group).
6. Delete file manually: remove a specific file by entering its full path (requires confirmation).
7. Exit.

## Notes & Limitations

- Duplicate detection is hash-based; identical content -> same hash. Different metadata or names do not affect detection.
- Scanning large directories may take considerable time and CPU due to hashing — consider excluding large binary folders if not needed.
- The tool does not perform automatic deletions without user confirmation.
- Always review recommended deletions before removing files; consider backups for important data.

## Contributing

Contributions, bug reports, and enhancements are welcome. To contribute:

1. Fork the repository.
2. Create a branch for your change.
3. Add tests or manual verification steps if applicable.
4. Open a pull request describing your change.

## License

This project is provided as-is. Add your preferred open-source license here (e.g., MIT) or update to match your project's licensing policy.

---

For more details, see the source in the `src/` directory or run the application and explore menu options.

