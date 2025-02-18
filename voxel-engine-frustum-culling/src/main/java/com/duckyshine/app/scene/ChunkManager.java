package com.duckyshine.app.scene;

import java.util.Map;
import java.util.Deque;
import java.util.HashMap;
import java.util.ArrayDeque;

import org.joml.Vector3f;
import org.joml.Vector3i;

import com.duckyshine.app.math.Voxel;

import com.duckyshine.app.model.Mesh;
import com.duckyshine.app.model.Block;
import com.duckyshine.app.model.Chunk;
import com.duckyshine.app.model.BlockType;

import com.duckyshine.app.physics.ray.RayResult;

import com.duckyshine.app.debug.Debug;

public class ChunkManager {
    public static final int CHUNK_WIDTH = 16;
    public static final int CHUNK_DEPTH = 16;
    public static final int CHUNK_HEIGHT = 16;

    private Map<Vector3i, Chunk> chunks;

    private Deque<Chunk> chunkQueue;

    public ChunkManager() {
        this.chunks = new HashMap<>();

        this.chunkQueue = new ArrayDeque<>();
    }

    public void initialise() {
        for (int x = 0; x < 1; x++) {
            for (int z = 0; z < 1; z++) {
                Chunk chunk = new Chunk(x * 16, 0, z * 16);

                this.chunkQueue.add(chunk);
            }
        }
    }

    public boolean isChunkActive(int x, int y, int z) {
        Vector3i position = new Vector3i(x, y, z);

        return this.chunks.containsKey(position);
    }

    public boolean isChunkActive(Vector3i position) {
        return this.chunks.containsKey(position);
    }

    public boolean isBlockActiveAtGlobalPosition(float x, float y, float z) {
        Vector3f position = new Vector3f(x, y, z);

        return this.isBlockActiveAtGlobalPosition(position);
    }

    public boolean isBlockActiveAtGlobalPosition(Vector3f position) {
        Chunk chunk = this.getChunkFromGlobalPosition(position);

        if (chunk == null) {
            return false;
        }

        Vector3i blockPosition = Voxel.getBlockPositionFromGlobalPosition(position);

        return chunk.isBlockActive(blockPosition);
    }

    public Chunk getChunk(int x, int y, int z) {
        Vector3i position = new Vector3i(x, y, z);

        return this.getChunk(position);
    }

    public Chunk getChunk(Vector3i position) {
        return this.chunks.get(position);
    }

    public Chunk getChunkFromGlobalPosition(float x, float y, float z) {
        Vector3f position = new Vector3f(x, y, z);

        return this.getChunkFromGlobalPosition(position);
    }

    public Chunk getChunkFromGlobalPosition(Vector3f position) {
        Vector3i chunkPosition = Voxel.getChunkPositionFromGlobalPosition(position);

        if (!this.isChunkActive(chunkPosition)) {
            return null;
        }

        return this.getChunk(chunkPosition);
    }

    public Block getBlockFromGlobalPosition(float x, float y, float z) {
        Vector3f position = new Vector3f(x, y, z);

        return this.getBlockFromGlobalPosition(position);
    }

    public Block getBlockFromGlobalPosition(Vector3f position) {
        Vector3i chunkPosition = Voxel.getChunkPositionFromGlobalPosition(position);

        if (!this.isChunkActive(chunkPosition)) {
            return null;
        }

        Chunk chunk = this.getChunk(chunkPosition);

        Vector3i blockPosition = Voxel.getBlockPositionFromGlobalPosition(position);

        return chunk.getBlock(blockPosition);
    }

    public void addBlock(RayResult rayResult) {
        Vector3f axes = rayResult.getAxes();
        Vector3f position = rayResult.getPosition();
        Vector3f delta = position.add(axes, new Vector3f());

        Chunk chunk = this.getChunkFromGlobalPosition(delta);

        if (chunk == null) {
            return;
        }

        Vector3i blockPosition = Voxel.getBlockPositionFromGlobalPosition(delta);

        Debug.debug(chunk.getPosition(), blockPosition, delta);

        chunk.addBlock(blockPosition, BlockType.GRASS);

        this.chunkQueue.add(chunk);
    }

    public void removeBlock(Vector3f position) {
        Chunk chunk = this.getChunkFromGlobalPosition(position);

        if (chunk == null) {
            return;
        }

        Vector3i blockPosition = Voxel.getBlockPositionFromGlobalPosition(position);

        chunk.removeBlock(blockPosition);

        this.chunkQueue.add(chunk);
    }

    public void update() {
        while (!this.chunkQueue.isEmpty()) {
            Chunk chunk = this.chunkQueue.poll();

            Vector3i chunkPosition = chunk.getPosition();

            if (!this.isChunkActive(chunkPosition)) {
                chunk.generate();

                this.chunks.put(chunkPosition, chunk);
            } else {
                chunk.update();
            }
        }
    }

    public void render() {
        for (Chunk chunk : this.chunks.values()) {
            Mesh mesh = chunk.getMesh();

            mesh.render();
        }
    }

    public void cleanup() {
        for (Chunk chunk : this.chunks.values()) {
            Mesh mesh = chunk.getMesh();

            mesh.cleanup();
        }
    }
}
