package com.duckyshine.app.scene;

import java.util.Map;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;

import org.joml.Vector3f;
import org.joml.Vector3i;

import com.duckyshine.app.physics.AABB;

import com.duckyshine.app.physics.controller.Player;
import com.duckyshine.app.physics.ray.RayResult;
import com.duckyshine.app.asset.AssetPool;

import com.duckyshine.app.camera.Camera;

import com.duckyshine.app.math.Axis;

import com.duckyshine.app.model.Mesh;
import com.duckyshine.app.model.Block;
import com.duckyshine.app.model.BlockType;
import com.duckyshine.app.model.Chunk;

import com.duckyshine.app.shader.Shader;
import com.duckyshine.app.shader.ShaderType;

import com.duckyshine.app.debug.Debug;

// Only two places where I need to change the constants, here and Chunk
public class Scene {
    private final int CHUNK_WIDTH = 16;
    private final int CHUNK_DEPTH = 16;
    private final int CHUNK_HEIGHT = 16;

    private Shader shader;

    private Player player;

    private Map<Vector3i, Chunk> chunks;

    private Deque<Chunk> chunkQueue;

    public Scene() {
        this.chunks = new HashMap<>();

        this.player = new Player(0.0f, 20.0f, 0.0f);

        this.shader = AssetPool.getShader(ShaderType.WORLD.getName());

        this.chunkQueue = new ArrayDeque<>();
    }

    public Scene(Shader shader) {
        this.chunks = new HashMap<>();

        this.player = new Player();

        this.shader = shader;

        this.chunkQueue = new ArrayDeque<>();
    }

    public void generate() {
        for (int x = 0; x < 3; x++) {
            for (int z = 0; z < 3; z++) {
                Chunk chunk = new Chunk(x * 16, 0, z * 16);

                chunk.generate();

                this.chunks.put(chunk.getPosition(), chunk);
            }
        }
    }

    public boolean isChunkActive(Vector3i position) {
        return this.chunks.containsKey(position);
    }

    public boolean isChunkActive(int x, int y, int z) {
        Vector3i position = new Vector3i(x, y, z);

        return this.isChunkActive(position);
    }

    public Chunk getChunk(int x, int y, int z) {
        Vector3i position = new Vector3i(x, y, z);

        return this.getChunk(position);
    }

    public Chunk getChunk(Vector3i position) {
        return this.chunks.get(position);
    }

    public Block getBlock(int x, int y, int z) {
        Vector3i position = new Vector3i(x, y, z);

        return this.getBlock(position);
    }

    public Vector3i getChunkPosition(int x, int y, int z) {
        Vector3i position = new Vector3i(x, y, z);

        return this.getChunkPosition(position);
    }

    public Vector3i getChunkPosition(Vector3i position) {
        if (position == null) {
            return null;
        }

        int x = (int) Math.floor((float) position.x / this.CHUNK_WIDTH) * this.CHUNK_WIDTH;
        int y = (int) Math.floor((float) position.y / this.CHUNK_HEIGHT) * this.CHUNK_HEIGHT;
        int z = (int) Math.floor((float) position.z / this.CHUNK_DEPTH) * this.CHUNK_DEPTH;

        return new Vector3i(x, y, z);
    }

    // Expect world position
    public Vector3i getBlockPosition(Vector3f position) {
        int x = (int) Math.floor(position.x);
        int y = (int) Math.floor(position.y);
        int z = (int) Math.floor(position.z);

        return new Vector3i(x % this.CHUNK_WIDTH, y % this.CHUNK_HEIGHT, z % this.CHUNK_DEPTH);
    }

    public boolean isBlockActive(Vector3i position) {
        Vector3i chunkPosition = this.getChunkPosition(position);

        if (!this.isChunkActive(chunkPosition)) {
            return false;
        }

        Vector3i blockPosition = this.getBlockPosition(new Vector3f(position));

        Chunk chunk = this.getChunk(chunkPosition);

        return chunk.isBlockActive(blockPosition);
    }

    public boolean isBlockActive(int x, int y, int z) {
        Vector3i position = new Vector3i(x, y, z);

        return this.isBlockActive(position);
    }

    public void removeBlock(Vector3i position) {
        Vector3i chunkPosition = this.getChunkPosition(position);

        if (chunkPosition == null) {
            return;
        }

        Chunk chunk = this.chunks.get(chunkPosition);

        chunk.removeBlock(position);

        this.chunkQueue.add(chunk);
    }

    public void addBlock(RayResult rayResult) {
        Vector3i axes = rayResult.getAxes();
        Vector3i position = rayResult.getPosition();

        Vector3i chunkPosition = this.getChunkPosition(position);

        if (!this.isChunkActive(chunkPosition)) {
            return;
        }

        Chunk chunk = this.chunks.get(chunkPosition);

        chunk.addBlock(position.add(axes, new Vector3i()), BlockType.GRASS);

        this.chunkQueue.add(chunk);
    }

    public Block getBlock(Vector3i position) {
        Vector3i chunkPosition = this.getChunkPosition(position);

        if (!this.isChunkActive(chunkPosition)) {
            return null;
        }

        Chunk chunk = this.getChunk(chunkPosition);

        Vector3i blockPosition = this.getBlockPosition(new Vector3f(position));

        return chunk.getBlock(blockPosition);
    }

    public boolean isColliding(AABB aabb) {
        Vector3f min = aabb.getMin();
        Vector3f max = aabb.getMax();

        int minX = (int) Math.floor(min.x);
        int minY = (int) Math.floor(min.y);
        int minZ = (int) Math.floor(min.z);

        int maxX = (int) Math.floor(max.x);
        int maxY = (int) Math.floor(max.y);
        int maxZ = (int) Math.floor(max.z);

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    if (this.isBlockActive(x, y, z)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public void checkCollisions(long window, float deltaTime) {
        this.player.updateVelocity(window, deltaTime);

        Vector3f position = this.player.getPosition();
        Vector3f target = this.player.getNextPosition(deltaTime);

        target.x = this.getAxisCollision(position.x, target.x, Axis.X);
        target.y = this.getAxisCollision(position.y, target.y, Axis.Y);
        target.z = this.getAxisCollision(position.z, target.z, Axis.Z);

        this.player.setPosition(target);
    }

    public float getAxisCollision(float position, float target, Axis axis) {
        AABB aabb = this.player.getAABB();

        AABB offsetAABB = aabb.getOffset(target - position, axis);

        boolean isColliding = this.isColliding(offsetAABB);

        if (axis == Axis.Y && isColliding && target <= position) {
            this.player.setIsGrounded(true);
        } else if (axis == Axis.Y) {
            this.player.setIsGrounded(false);
        }

        return isColliding ? position : target;
    }

    public void update(long window, float deltaTime) {
        this.checkCollisions(window, deltaTime);

        this.player.update(window, this);

        // This can be multithreaded with the addition of queuing- in another
        // optimisation
        while (!this.chunkQueue.isEmpty()) {
            Chunk chunk = this.chunkQueue.poll();

            chunk.update();
        }
    }

    public void setShader(ShaderType shaderType) {
        Camera camera = this.player.getCamera();

        this.shader = AssetPool.getShader(shaderType.getName());

        this.shader.use();

        this.shader.setMatrix4f("projectionViewMatrix", camera.getProjectionView());
    }

    public void render() {
        this.setShader(ShaderType.WORLD);

        for (Chunk chunk : chunks.values()) {
            Mesh mesh = chunk.getMesh();

            mesh.render();
        }

        AABB aabb = this.player.getAABB();

        aabb.loadBuffer();
        this.setShader(ShaderType.AABB);
        aabb.render();
    }

    public void cleanup() {
        for (Chunk chunk : chunks.values()) {
            Mesh mesh = chunk.getMesh();

            mesh.cleanup();
        }
    }

    public Camera getCamera() {
        return this.player.getCamera();
    }
}
