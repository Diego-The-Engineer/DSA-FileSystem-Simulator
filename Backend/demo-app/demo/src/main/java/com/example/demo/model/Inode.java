package com.example.demo.model;
import java.util.*;

public class Inode {
    private String name;
    private long size;
    private Date creationDate;
    private String owner;
    private int permissions;
    private DataType type;
    private List<Integer> blockId;
    private Map<String, Inode> children;
    private Inode parent;

    public Inode(String name, DataType type) {
        this.name = name;
        this.type = type;
        this.size = 0;
        this.creationDate = new Date();

        if(type == DataType.DIRECTORY) {
            this.children = new HashMap<>();
        } else {
            this.blockId = new ArrayList<>();
        }
    }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public DataType getType() { return type; }
    public void setType(DataType type) { this.type = type; }
    public long getSize() { return size; }
    public void setSize(long size) { this.size = size; }
    public Map<String, Inode> getChildren() { return children; }
    public List<Integer> getBlockId() { return blockId; }
    public Inode getParent() { return parent; }
    public void setParent(Inode parent) { this.parent = parent; }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public int getPermissions() {
        return permissions;
    }

    public void setPermissions(int permissions) {
        this.permissions = permissions;
    }

    public void setBlockId(List<Integer> blockId) {
        this.blockId = blockId;
    }

    public void setChildren(Map<String, Inode> children) {
        this.children = children;
    }
}