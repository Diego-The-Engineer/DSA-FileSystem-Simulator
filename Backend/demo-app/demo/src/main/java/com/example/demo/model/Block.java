package com.example.demo.model;

public class Block {
    private int id;
    private byte[] data;
    private boolean occupied;

    public Block(int id, int size) {
        this.id = id;
        this.data = new byte[size];
        this.occupied = false;
    }

    public int getId() { return id; }
    public byte[] getData() { return data; }
    public boolean isOccupied() { return occupied; }

    public void setData(byte[] data) {
        this.data = data;
        this.occupied = true;
    }

    public void freeBlock() {
        this.data = new byte[this.data.length];
        this.occupied = false;
    }
}