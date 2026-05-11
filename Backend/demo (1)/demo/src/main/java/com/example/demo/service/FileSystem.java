package com.example.demo.service;

import com.example.demo.model.DataType;
import com.example.demo.model.Inode;
import com.example.demo.model.Block;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class FileSystem {
    private Inode root;
    private Inode currentDirectory;
    private List<Block> disk;
    private final int BLOCK_SIZE = 1024;
    private final int TOTAL_BLOCKS = 100;

    public FileSystem() {
        this.root = new Inode("/", DataType.DIRECTORY);
        this.currentDirectory = root;
        this.disk = new ArrayList<>();
        for (int i = 0; i < TOTAL_BLOCKS; i++) {
            disk.add(new Block(i, BLOCK_SIZE));
        }
    }

    public String mkdir(String path) {
        if(currentDirectory.getType() != DataType.DIRECTORY) {
            return "Error: No such directory";
        }
        Map<String, Inode> children = currentDirectory.getChildren();
        if(children.containsKey(path)) {
            return "Error: Directory already exists";
        }
        Inode newDirectory = new Inode(path, DataType.DIRECTORY);
        newDirectory.setParent(currentDirectory);
        children.put(path, newDirectory);
        return "Directory " + newDirectory.getName() + " is created";
    }

    public List<String> ls(){
        if(currentDirectory.getType() != DataType.DIRECTORY) {
            return new ArrayList<>();
        }
        return new ArrayList<>(currentDirectory.getChildren().keySet());
    }

    public String touch(String path) {
        Map<String, Inode> children = currentDirectory.getChildren();
        if(children.containsKey(path)) return "Error: File already exists";

        Inode file = new Inode(path, DataType.FILE);
        file.setParent(currentDirectory);
        children.put(path, file);
        return "File " + file.getName() + " is created";
    }

    public String cd(String path) {
        if(path.equals("..")) {
            if (currentDirectory.getParent() != null) {
                currentDirectory = currentDirectory.getParent();
                return "Moved to " + currentDirectory.getName();
            } else {
                return "Already at root directory";
            }
        }
        Map<String, Inode> children = currentDirectory.getChildren();
        if(children.containsKey(path)) {
            Inode target = children.get(path);
            if(target.getType() == DataType.DIRECTORY) {
                currentDirectory = target;
                return "Moved to " + currentDirectory.getName();
            } else {
                return "Error: " + path + " is a file, not a directory";
            }
        }
        return "Not found";
    }
    public String echo(String text, String file){
        Map<String, Inode> children = currentDirectory.getChildren();
        Inode FILE;
        if(children.containsKey(text)) {
            FILE = children.get(text);
            if(FILE.getType() == DataType.DIRECTORY) {
                return "NOT FILE";
            }
            for(int id : FILE.getBlockId()) disk.get(id).freeBlock();
            FILE.getBlockId().clear();
        }else{
            FILE = new Inode(file, DataType.FILE);
            FILE.setParent(currentDirectory);
            children.put(file, FILE);
        }

        byte[] bytes = text.getBytes();
        FILE.setSize(bytes.length);

        int blocksNeed = (int)Math.ceil((double)file.length()/(double)BLOCK_SIZE);
        if(blocksNeed == 0) blocksNeed = 1;
        int blocksAllocated = 0;
        int offset = 0;
        for (Block block : disk) {
            if (!block.isOccupied()) {
                int chunkSize = Math.min(BLOCK_SIZE, bytes.length - offset);
                byte[] chunk = new byte[chunkSize];
                System.arraycopy(bytes, offset, chunk, 0, chunkSize);

                block.setData(chunk);
                FILE.getBlockId().add(block.getId());

                offset += chunkSize;
                blocksAllocated++;
                if (blocksAllocated == blocksNeed) break;
            }
        }
        if(blocksAllocated < blocksNeed) return "No space enough";
        return "OK";
    }
    public String cat(String fileName) {
        Map<String, Inode> children = currentDirectory.getChildren();

        if (!children.containsKey(fileName)) {
            return "Error: File not exists";
        }

        Inode file = children.get(fileName);
        if (file.getType() == DataType.DIRECTORY) {
            return "Error: " + fileName + " is a directory";
        }

        StringBuilder content = new StringBuilder();
        for (int blockId : file.getBlockId()) {
            Block block = disk.get(blockId);
            if (block.getData() != null) {
                content.append(new String(block.getData()).trim());
            }
        }
        return content.toString();
    }
    public String tree(){
        StringBuilder output = new StringBuilder();
        output.append("\n");
        treeRecursive(currentDirectory, "", output);
        return output.toString();
    }

    private void treeRecursive(Inode node, String prefix, StringBuilder output) {
        if(node.getType() != DataType.DIRECTORY) return;
        List<String> childrenNames = new ArrayList<>(node.getChildren().keySet());
        for(int i=0; i<childrenNames.size(); i++){
            String childName = childrenNames.get(i);
            Inode  childNode = node.getChildren().get(childName);

            boolean isLast = (i == childrenNames.size()-1);
            output.append(prefix);
            output.append(isLast ? "└── " : "├── ");
            output.append(childName).append("\n");

            if(childNode.getType() == DataType.DIRECTORY){
                String prefixChild = prefix + (isLast ? "    " : "│   ");
                treeRecursive(childNode, prefixChild, output);
            }
        }
    }
    public String manual(){
        return "mkdir [directory name] : create directories\n\n" +
                "touch [file name]: create new files\n\n" +
                "cd ..: return to the previous directory\n\n" +
                "tree: displays the tree structure for the file system\n\n" +
                "ls: list all the directories and files from the current directory\n\n" +
                "echo [text] > [file name]: writes content in a file\n\n" +
                "cat: displays the content from a file\n\n";
    }
}