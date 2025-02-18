package com.duckyshine.app.math;

import org.joml.Vector3f;
import org.joml.Vector3i;

public class Voxel {
    public static final int CHUNK_WIDTH = 16;
    public static final int CHUNK_DEPTH = 16;
    public static final int CHUNK_HEIGHT = 16;

    public static Vector3i getChunkPositionFromGlobalPosition(float x, float y, float z) {
        Vector3f position = new Vector3f(x, y, z);

        return Voxel.getChunkPositionFromGlobalPosition(position);
    }

    public static Vector3i getChunkPositionFromGlobalPosition(Vector3f position) {
        int x = Math.floorDiv((int) position.x, Voxel.CHUNK_WIDTH) * Voxel.CHUNK_WIDTH;
        int y = Math.floorDiv((int) position.y, Voxel.CHUNK_HEIGHT) * Voxel.CHUNK_HEIGHT;
        int z = Math.floorDiv((int) position.z, Voxel.CHUNK_DEPTH) * Voxel.CHUNK_DEPTH;

        return new Vector3i(x, y, z);
    }

    public static Vector3i getBlockPositionFromGlobalPosition(float x, float y, float z) {
        Vector3f position = new Vector3f(x, y, z);

        return Voxel.getBlockPositionFromGlobalPosition(position);
    }

    public static Vector3i getBlockPositionFromGlobalPosition(Vector3f position) {
        int x = Math.floorMod((int) position.x, Voxel.CHUNK_WIDTH);
        int y = Math.floorMod((int) position.y, Voxel.CHUNK_HEIGHT);
        int z = Math.floorMod((int) position.z, Voxel.CHUNK_DEPTH);

        return new Vector3i(x, y, z);
    }
}
