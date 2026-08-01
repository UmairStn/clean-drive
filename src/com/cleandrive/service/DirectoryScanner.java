package com.cleandrive.service;

import com.cleandrive.datastructures.AVLTree;
import com.cleandrive.datastructures.DirectoryGraph;
import com.cleandrive.datastructures.MaxHeap;
import com.cleandrive.model.FileRecord;
import com.cleandrive.model.FolderNode;

import java.io.File;
import java.time.Instant;

public class DirectoryScanner {

    public DirectoryGraph scanDirectoryDFS(String rootPath, AVLTree avlTree, MaxHeap maxHeap) {
        File rootDir = new File(rootPath);
        if (!rootDir.exists() || !rootDir.isDirectory()) {
            return null;
        }

        DirectoryGraph graph = new DirectoryGraph(rootPath);
        avlTree.clear();
        maxHeap.clear();

        traverseDFS(graph.getRoot(), avlTree, maxHeap);
        return graph;
    }

    private void traverseDFS(FolderNode currentFolder, AVLTree avlTree, MaxHeap maxHeap) {
        File dir = new File(currentFolder.getFolderPath());
        File[] fileList = dir.listFiles();

        if (fileList == null) return;

        for (File file : fileList) {
            if (file.isDirectory()) {
                FolderNode childFolder = new FolderNode(file.getAbsolutePath());
                currentFolder.addSubfolder(childFolder);
                traverseDFS(childFolder, avlTree, maxHeap);
            } else {
                FileRecord record = new FileRecord(
                        file.getAbsolutePath(),
                        file.getName(),
                        file.length(),
                        Instant.ofEpochMilli(file.lastModified())
                );

                String hash = HashGenerator.generateSHA256(record.getFilePath());
                record.setFileHash(hash);

                currentFolder.addFile(record);
                avlTree.insert(record);
                maxHeap.insert(record);
            }
        }
    }
}