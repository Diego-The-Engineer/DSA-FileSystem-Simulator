package com.example.demo.service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.springframework.stereotype.Service;

import com.example.demo.model.Block;
import com.example.demo.model.DataType;
import com.example.demo.model.Inode;

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
                "cat: displays the content from a file\n\n" +
                "rm -r: remove a file or directory\n\n" + 
                "mv [file] [destiny]: move a file to another place\n\n" + 
                "find [file]: find a path from the file destiny";
    }
    public String rm(String argument) {
        boolean recursive = false;
        String targetName = argument;
        if (argument.startsWith("-r ")) {
            recursive = true;
            targetName = argument.substring(3).trim(); 
        }

        Map<String, Inode> children = currentDirectory.getChildren();
        
        if (!children.containsKey(targetName)) {
            return "rm: no se puede borrar '" + targetName + "': No existe el archivo o directorio";
        }

        Inode target = children.get(targetName);
        if (target.getType() == DataType.DIRECTORY) {
            if (!recursive) {
                return "rm: no se puede borrar '" + targetName + "': Es un directorio (usa rm -r)";
            }
            deleteNode(target);
            children.remove(targetName);
            return "Directorio '" + targetName + "' y todo su contenido eliminado.";
        } else {
            deleteNode(target);
            children.remove(targetName);
            return "Archivo '" + targetName + "' eliminado.";
        }
    }
    private void deleteNode(Inode node) {
        if (node.getType() == DataType.DIRECTORY) {
            List<Inode> childrenList = new ArrayList<>(node.getChildren().values());
            for (Inode child : childrenList) {
                deleteNode(child);
            }
            node.getChildren().clear();
        } else {
            for (int blockId : node.getBlockId()) {
                Block block = disk.get(blockId);
                block.setData(null); 
                
            }
            node.getBlockId().clear();
        }
    }

    public String mv(String argument) {
        String[] parts = argument.split(" ");
        if (parts.length != 2) {
            return "mv: sintaxis incorrect. Use: mv <origin> <destiny>";
        }

        String sourceName = parts[0];
        String destName = parts[1];

        Map<String, Inode> children = currentDirectory.getChildren();
        if (!children.containsKey(sourceName)) {
            return "mv: can not moved '" + sourceName + "': not exist";
        }

        Inode sourceNode = children.get(sourceName);
        if (children.containsKey(destName) && children.get(destName).getType() == DataType.DIRECTORY) {
            Inode destDir = children.get(destName);
            children.remove(sourceName);
            sourceNode.setParent(destDir);
            destDir.getChildren().put(sourceName, sourceNode);
            
            return "Moved '" + sourceName + "' to '" + destName + "/'";
        } 
        else if (!children.containsKey(destName)) {
            children.remove(sourceName);
            sourceNode.setName(destName); 
            children.put(destName, sourceNode);
            
            return "Renamed '" + sourceName + "' to '" + destName + "'";
        } 
        else {
            return "mv: destiny '" + destName + "' already exist.";
        }
    }
    public String find(String targetName) {
        Queue<Inode> queue = new LinkedList<>();
        queue.add(currentDirectory);
        
        StringBuilder results = new StringBuilder();
        while (!queue.isEmpty()) {
            Inode current = queue.poll(); 

            if (current.getName() != null && current.getName().equals(targetName)) {
                results.append(getFullPath(current)).append("\n");
            }
            
            
            if (current.getType() == DataType.DIRECTORY) {
                queue.addAll(current.getChildren().values());
            }
        }
        
        if (results.length() == 0) {
            return "find: Not results to: '" + targetName + "'";
        }
        
        return results.toString().trim();
    }

    private String getFullPath(Inode node) {
        if (node.getParent() == null) return "/"; 
        
        List<String> pathParts = new ArrayList<>();
        Inode temp = node;
        while (temp.getParent() != null) {
            pathParts.add(0, temp.getName()); 
            temp = temp.getParent();
        }
        
        return "/" + String.join("/", pathParts);
    }
}