package com.duckyshine.app.scene;

import java.util.Map;
import java.util.Set;
import java.util.Deque;
import java.util.HashMap;
import java.util.ArrayDeque;

import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector3i;

import com.duckyshine.app.math.Vector2;
import com.duckyshine.app.math.Voxel;

import com.duckyshine.app.model.Mesh;
import com.duckyshine.app.model.Block;
import com.duckyshine.app.model.Chunk;
import com.duckyshine.app.model.BlockType;

import com.duckyshine.app.physics.ray.RayResult;

import com.duckyshine.app.debug.Debug;

public class ChunkManager {
    public final int CHUNK_WIDTH = 16;
    public final int CHUNK_DEPTH = 16;
    public final int CHUNK_HEIGHT = 16;

    private Map<Vector3i, Chunk> chunks;

    private Map<Vector2i, HeightMap> heightMaps;

    private Deque<Vector3i> chunkQueue;

    public ChunkManager() {
        this.chunks = new HashMap<>();

        this.heightMaps = new HashMap<>();

        this.chunkQueue = new ArrayDeque<>();
    }

    // Dynamically generate based on player's position
    public void initialise() {
        for (int x = 0; x < 3; x++) {
            for (int z = 0; z < 3; z++) {
                Vector3i chunkPosition = new Vector3i(x * 16, 0, z * 16);

                this.chunkQueue.add(chunkPosition);
            }
        }
    }

    public boolean isHeightMapGenerated(Vector3i position) {
        Vector2i heightMapPosition = Vector2.getXZInteger(position);

        return this.heightMaps.containsKey(heightMapPosition);
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

        this.chunkQueue.add(chunk.getPosition());
    }

    public void addHeightMap(Vector3i position) {
        HeightMap heightMap = new HeightMap(this.CHUNK_WIDTH, this.CHUNK_HEIGHT);

        heightMap.generate(position);

        this.heightMaps.put(Vector2.getXZInteger(position), heightMap);
    }

    public HeightMap getHeightMap(Vector3i position) {
        return this.heightMaps.get(Vector2.getXZInteger(position));
    }

    public void removeBlock(Vector3f position) {
        Chunk chunk = this.getChunkFromGlobalPosition(position);

        if (chunk == null) {
            return;
        }

        Vector3i blockPosition = Voxel.getBlockPositionFromGlobalPosition(position);

        chunk.removeBlock(blockPosition);

        this.chunkQueue.add(chunk.getPosition());
    }

    public void addChunk(Vector3i position) {
        Chunk chunk = new Chunk(position);

        HeightMap heightMap = this.getHeightMap(position);

        chunk.generate(this.chunkQueue, heightMap);

        this.chunks.put(position, chunk);
    }

    public void updateChunk(Vector3i position) {
        Chunk chunk = this.chunks.get(position);

        chunk.update();
    }

    public void update() {
        while (!this.chunkQueue.isEmpty()) {
            Vector3i chunkPosition = this.chunkQueue.poll();

            if (!this.isHeightMapGenerated(chunkPosition)) {
                this.addHeightMap(chunkPosition);
            }

            if (!this.isChunkActive(chunkPosition)) {
                this.addChunk(chunkPosition);
            } else {
                this.updateChunk(chunkPosition);
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
