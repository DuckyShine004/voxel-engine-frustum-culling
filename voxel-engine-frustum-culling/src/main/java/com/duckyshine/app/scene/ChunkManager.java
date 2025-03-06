package com.duckyshine.app.scene;

import java.util.Map;
import java.util.Set;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
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
import com.duckyshine.app.physics.controller.Player;
import com.duckyshine.app.physics.ray.RayResult;

import com.duckyshine.app.debug.Debug;

// MUST MULTITHREAD, MESH GENERATION AND NOISE IS SUPER SLOW
public class ChunkManager {
    public final int CHUNK_WIDTH = 16;
    public final int CHUNK_DEPTH = 16;
    public final int CHUNK_HEIGHT = 16;

    private Map<Vector3i, Chunk> chunks;

    private Map<Vector2i, HeightMap> heightMaps;

    private Set<Vector3i> queuedChunks;

    private Deque<Vector3i> chunkQueue;

    public ChunkManager() {
        this.chunks = new HashMap<>();

        this.heightMaps = new HashMap<>();

        this.queuedChunks = new HashSet<>();

        this.chunkQueue = new ArrayDeque<>();
    }

    // Dynamically generate based on player's position
    public void initialise() {
        for (int x = 0; x < 1; x++) {
            for (int z = 0; z < 1; z++) {
                Vector3i chunkPosition = new Vector3i(x * 16, 0, z * 16);

                this.queueChunk(chunkPosition);
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

        chunk.setIsUpdate(true);

        this.queueChunk(chunk.getPosition());
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

        chunk.setIsUpdate(true);

        this.queueChunk(chunk.getPosition());
    }

    public void queueChunk(Vector3i position) {
        Debug.debug(position);
        if (!this.queuedChunks.contains(position)) {
            this.chunkQueue.add(position);
            this.queuedChunks.add(position);
        }
    }

    public void addChunk(Vector3i position) {
        Chunk chunk = new Chunk(position);

        HeightMap heightMap = this.getHeightMap(position);

        chunk.generate(this, heightMap);

        this.chunks.put(position, chunk);
    }

    public void updateChunk(Vector3i position) {
        Chunk chunk = this.chunks.get(position);

        // are we actually updating the chunk
        if (chunk.getIsUpdate()) {
            chunk.update();

            chunk.setIsUpdate(false);
        }
    }

    public void addSurroundingChunks(Player player) {
        int renderDistance = player.getRenderDistance();

        Vector3i chunkPosition = Voxel.getChunkPositionFromGlobalPosition(player.getPosition());

        int startX = chunkPosition.x - (this.CHUNK_WIDTH * renderDistance);
        int startY = chunkPosition.y - (this.CHUNK_HEIGHT * renderDistance);
        int startZ = chunkPosition.z - (this.CHUNK_DEPTH * renderDistance);

        int endX = chunkPosition.x + (this.CHUNK_WIDTH * renderDistance);
        int endY = chunkPosition.y + (this.CHUNK_HEIGHT * renderDistance);
        int endZ = chunkPosition.z + (this.CHUNK_DEPTH * renderDistance);

        for (int x = startX; x <= endX; x += this.CHUNK_WIDTH) {
            for (int y = startY; y <= endY; y += this.CHUNK_HEIGHT) {
                for (int z = startZ; z <= endZ; z += this.CHUNK_DEPTH) {
                    Vector3i position = new Vector3i(x, y, z);

                    this.queueChunk(position);
                }
            }
        }
    }

    public void update(Player player) {
        this.addSurroundingChunks(player);

        while (!this.chunkQueue.isEmpty()) {
            Debug.debug(this.chunkQueue.size());
            Vector3i chunkPosition = this.chunkQueue.poll();

            if (!this.isHeightMapGenerated(chunkPosition)) {
                this.addHeightMap(chunkPosition);
            }

            // Current issue is that chunks are being updated not on purpose
            if (!this.isChunkActive(chunkPosition)) {
                this.addChunk(chunkPosition);
            } else {
                this.updateChunk(chunkPosition);
            }

            this.queuedChunks.remove(chunkPosition);
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
