package com.duckyshine.app.math;

import org.joml.Vector3i;

public enum Direction {
    TOP(0, 1, 0, 0),
    BOTTOM(0, -1, 0, 1),
    LEFT(-1, 0, 0, 2),
    RIGHT(1, 0, 0, 3),
    FRONT(0, 0, 1, 4),
    BACK(0, 0, -1, 5);

    private final int x;
    private final int y;
    private final int z;

    private final int index;

    private final Vector3i direction;

    private Direction(int x, int y, int z, int index) {
        this.x = x;
        this.y = y;
        this.z = z;

        this.index = index;

        this.direction = new Vector3i(x, y, z);
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getZ() {
        return this.z;
    }

    public int getIndex() {
        return this.index;
    }

    public Vector3i get() {
        return this.direction;
    }

    public String getName() {
        return this.name().toLowerCase();
    }
}
