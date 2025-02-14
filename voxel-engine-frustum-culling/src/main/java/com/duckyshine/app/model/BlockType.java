package com.duckyshine.app.model;

public enum BlockType {
    GRASS("grass", 0);

    private int index;

    private String type;

    private BlockType(String type, int index) {
        this.type = type;

        this.index = index;
    }

    public int getIndex() {
        return this.index;
    }

    public String getType() {
        return this.type;
    }

    public final String getName() {
        return this.name().toLowerCase();
    }
}
