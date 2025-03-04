package com.duckyshine.app.model;

import org.joml.Vector3i;

import com.duckyshine.app.math.Range;
import com.duckyshine.app.math.noise.Noise;
import com.duckyshine.app.debug.Debug;

public class Chunk {
    private final int WIDTH = 16;
    private final int DEPTH = 16;
    private final int HEIGHT = 16;

    private final Vector3i position;

    private Block[][][] blocks;

    private int[][] heightMap;

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
        this.blocks = new Block[WIDTH][HEIGHT][DEPTH];

        this.mesh = new Mesh();
    }

    public void generateHeightMap() {
        Vector3i position = this.getPosition();

        this.heightMap = new int[this.DEPTH][this.WIDTH];

        for (int dz = 0; dz < this.DEPTH; dz++) {
            for (int dx = 0; dx < this.WIDTH; dx++) {
                double offsetX = (double) (position.x + dx) / this.WIDTH - 0.5d;
                double offsetZ = (double) (position.z + dz) / this.DEPTH - 0.5d;

                this.heightMap[dz][dx] = Noise.getNoise2d(offsetX, offsetZ);
            }
        }

        // Check if the heightmap values are valid or not and update
        this.validateHeightMap();
    }

    public void validateHeightMap() {
        for (int z = 0; z < this.DEPTH; z++) {
            for (int x = 0; x < this.WIDTH; x++) {
                int height = heightMap[z][x];
                if (!Range.isInRange1D(height, this.position.y, this.position.y + height)) {
                    this.heightMap[z][x] = -1;
                }
            }
        }
    }

    public void generate() {
        if (this.heightMap == null) {
            this.generateHeightMap();
        }

        for (int z = 0; z < this.DEPTH; z++) {
            for (int x = 0; x < this.WIDTH; x++) {
                int y = this.heightMap[z][x];

                if (y == -1) {
                    continue;
                }

                this.addBlock(x, y, z, BlockType.GRASS);
            }
        }

        this.update();
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
