package com.cleandrive.datastructures;

import com.cleandrive.model.FileRecord;
import java.util.ArrayList;
import java.util.List;

public class MaxHeap {
    private List<FileRecord> heap = new ArrayList<>();

    public void insert(FileRecord record) {
        heap.add(record);
        heapifyUp(heap.size() - 1);
    }

    public FileRecord extractMax() {
        if (heap.isEmpty()) return null;
        FileRecord max = heap.get(0);
        FileRecord last = heap.remove(heap.size() - 1);
        if (!heap.isEmpty()) {
            heap.set(0, last);
            heapifyDown(0);
        }
        return max;
    }

    private void heapifyUp(int index) {
        while (index > 0) {
            int parent = (index - 1) / 2;
            if (heap.get(index).getSizeInBytes() > heap.get(parent).getSizeInBytes()) {
                swap(index, parent);
                index = parent;
            } else {
                break;
            }
        }
    }

    private void heapifyDown(int index) {
        int size = heap.size();
        while (index < size) {
            int left = 2 * index + 1;
            int right = 2 * index + 2;
            int largest = index;

            if (left < size && heap.get(left).getSizeInBytes() > heap.get(largest).getSizeInBytes()) {
                largest = left;
            }
            if (right < size && heap.get(right).getSizeInBytes() > heap.get(largest).getSizeInBytes()) {
                largest = right;
            }

            if (largest != index) {
                swap(index, largest);
                index = largest;
            } else {
                break;
            }
        }
    }

    private void swap(int i, int j) {
        FileRecord temp = heap.get(i);
        heap.set(i, heap.get(j));
        heap.set(j, temp);
    }

    public boolean isEmpty() {
        return heap.isEmpty();
    }

    public MaxHeap cloneHeap() {
        MaxHeap copy = new MaxHeap();
        copy.heap = new ArrayList<>(this.heap);
        return copy;
    }

    public void clear() {
        heap.clear();
    }

/**
     * Retrieves the largest FileRecord without removing it from the heap.
     * Time Complexity: $O(1)$
     */
    public FileRecord peekMax() {
        if (heap.isEmpty()) return null;
        return heap.get(0);
    }

    /**
     * Safely retrieves the top N largest files for UI display.
     * Uses cloneHeap() to ensure the original MaxHeap remains fully intact.
     * Time Complexity: $O(k \log n)$ where k is the limit
     */
    public List<FileRecord> getTopNFiles(int n) {
        List<FileRecord> topFiles = new ArrayList<>();
        
        // 1. Create a safe, temporary copy of the heap
        MaxHeap tempHeap = this.cloneHeap();
        
        // 2. Prevent IndexOutOfBounds if they ask for 10 files but only 3 exist
        int limit = Math.min(n, this.heap.size());
        
        // 3. Extract the top files from the temporary heap
        for (int i = 0; i < limit; i++) {
            topFiles.add(tempHeap.extractMax());
        }
        
        return topFiles;
    }

}