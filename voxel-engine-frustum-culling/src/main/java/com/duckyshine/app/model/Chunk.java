package com.duckyshine.app.model;

import java.util.Deque;

import org.joml.Vector3i;

import com.duckyshine.app.math.Range;
import com.duckyshine.app.math.Voxel;
import com.duckyshine.app.math.noise.Noise;
import com.duckyshine.app.scene.ChunkManager;
import com.duckyshine.app.scene.HeightMap;
import com.duckyshine.app.debug.Debug;

public class Chunk {
    private final int WIDTH = 16;
    private final int DEPTH = 16;
    private final int HEIGHT = 16;

    private final Vector3i position;

    private boolean isHidden;
    private boolean isUpdate;

    private Block[][][] blocks;

    private Mesh mesh;

    public Chunk(Vector3i position) {
        this.position = position;

        this.initialise();
    }

    public Chunk(int x, int y, int z) {
        this.position = new Vector3i(x, y, z);

        this.initialise();
    }

    public void initialise() {
        this.isHidden = false;
        this.isUpdate = false;

        this.blocks = new Block[WIDTH][HEIGHT][DEPTH];

        this.mesh = new Mesh();
    }

    public boolean isValidHeight(int height) {
        return Range.isInRange1D(height, this.position.y, this.position.y + this.HEIGHT);
    }

    public void generate(ChunkManager chunkManager, HeightMap heightMap) {
        int[][] heights = heightMap.getHeights();

        this.generateSurface(chunkManager, heights);

        this.update();
    }

    public void generateSurface(ChunkManager chunkManager, int[][] heights) {
        for (int z = 0; z < this.DEPTH; z++) {
            for (int x = 0; x < this.WIDTH; x++) {
                int y = heights[z][x];

                // Don't need this since, chunks are added based on player's position
                if (!isValidHeight(y)) {
                    Vector3i chunkPosition = Voxel.getChunkPositionFromGlobalPosition(this.position.x + x, y,
                            this.position.z + z);
                    chunkManager.queueChunk(chunkPosition);

                    continue;
                }

                this.addBlock(x, Math.floorMod(y, this.HEIGHT), z, BlockType.GRASS);
            }
        }
    }

    // Use 3d noise
    public void generateCaves() {

    }

    public void update() {
        this.mesh.update(this);
    }

    public void setMesh(Mesh mesh) {
        this.mesh = mesh;
    }

    public Mesh getMesh() {
        return this.mesh;
    }

    public boolean isBlockActive(Vector3i position) {
        return this.isBlockActive(position.x, position.y, position.z);
    }

    public boolean isBlockActive(int x, int y, int z) {
        Vector3i position = new Vector3i(x, y, z);

        if (!Range.isInRange3D(position, this.WIDTH, this.HEIGHT, this.DEPTH)) {
            return false;
        }

        return this.blocks[x][y][z] != null;
    }

    // Assumes args are valid
    public void addBlock(Vector3i position, BlockType blockType) {
        this.addBlock(position.x, position.y, position.z, blockType);
    }

    public void addBlock(int x, int y, int z, BlockType blockType) {
        Block block = new Block(x, y, z, blockType);

        block.setGlobalPosition(this.position.x + x, this.position.y + y, this.position.z + z);

        this.blocks[x][y][z] = block;
    }

    public void removeBlock(Vector3i position) {
        this.removeBlock(position.x, position.y, position.z);
    }

    public void removeBlock(int x, int y, int z) {
        this.blocks[x][y][z] = null;
    }

    public Block getBlock(int x, int y, int z) {
        return this.blocks[x][y][z];
    }

    public Block getBlock(Vector3i position) {
        return this.getBlock(position.x, position.y, position.z);
    }

    public Vector3i getPosition() {
        return this.position;
    }

    public boolean isHidden() {
        return this.isHidden;
    }

    public void setIsUpdate(boolean isUpdate) {
        this.isUpdate = isUpdate;
    }

    public boolean getIsUpdate() {
        return this.isUpdate;
    }

    public int getWidth() {
        return this.WIDTH;
    }

    public int getHeight() {
        return this.HEIGHT;
    }

    public int getDepth() {
        return this.DEPTH;
    }
}
