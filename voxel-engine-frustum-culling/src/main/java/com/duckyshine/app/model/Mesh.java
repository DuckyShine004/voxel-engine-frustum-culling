package com.duckyshine.app.model;

import java.util.List;
import java.util.ArrayList;

import org.joml.Vector3i;

import com.duckyshine.app.math.Direction;

import com.duckyshine.app.math.noise.Noise;

import com.duckyshine.app.model.texture.Texture;

import com.duckyshine.app.buffer.Buffer;
import com.duckyshine.app.buffer.BufferData;
import com.duckyshine.app.buffer.MeshBuffer;

import com.duckyshine.app.debug.Debug;

import static org.lwjgl.opengl.GL30.*;

public class Mesh {
    private Buffer buffer;

    private List<Quad> quads;

    private List<Float> vertices;
    private List<Float> coordinates;

    private List<Integer> indices;
    private List<Integer> textures;

    private int[][] heightMap;

    public Mesh() {
        this.buffer = new MeshBuffer();

        this.quads = new ArrayList<>();

        this.vertices = new ArrayList<>();
        this.coordinates = new ArrayList<>();

        this.indices = new ArrayList<>();
        this.textures = new ArrayList<>();
    }

    public void update(Chunk chunk) {
        this.cull(chunk);

        this.merge(chunk);

        this.build();
    }

    private void cull(Chunk chunk) {
        int width = chunk.getWidth();
        int depth = chunk.getDepth();
        int height = chunk.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                for (int z = 0; z < depth; z++) {
                    if (!chunk.isBlockActive(x, y, z)) {
                        continue;
                    }

                    Block block = chunk.getBlock(x, y, z);

                    this.cullFacesForBlock(chunk, block);
                }
            }
        }
    }

    private void cullFacesForBlock(Chunk chunk, Block block) {
        Vector3i position = block.getPosition();

        block.setAllFaceStatuses(true);

        for (Direction direction : Direction.values()) {
            int dx = position.x + direction.getX();
            int dy = position.y + direction.getY();
            int dz = position.z + direction.getZ();

            if (chunk.isBlockActive(dx, dy, dz)) {
                block.setFaceStatus(direction, false);
            }
        }
    }

    private int findMaximumHeight(BlockType[][] grid, int x, int y, int height) {
        BlockType blockType = grid[y][x];

        int maximumHeight = 1;

        while (true) {
            int deltaY = y + maximumHeight;

            if (deltaY >= height || grid[deltaY][x] != blockType) {
                break;
            }

            ++maximumHeight;
        }

        return maximumHeight;
    }

    private int findMaximumWidth(BlockType[][] grid, int x, int y, int width, int maximumHeight) {
        BlockType blockType = grid[y][x];

        int maximumWidth = 1;

        while (true) {
            int deltaX = x + maximumWidth;

            if (deltaX >= width) {
                break;
            }

            boolean isValid = true;

            for (int height = 0; height < maximumHeight; height++) {
                int deltaY = y + height;

                if (grid[deltaY][deltaX] != blockType) {
                    isValid = false;
                    break;
                }
            }

            if (isValid) {
                ++maximumWidth;
            } else {
                break;
            }
        }

        return maximumWidth;
    }

    private void resetGrid(BlockType[][] grid, int x, int y, int maximumWidth, int maximumHeight) {
        for (int height = 0; height < maximumHeight; height++) {
            for (int width = 0; width < maximumWidth; width++) {
                int deltaX = x + width;
                int deltaY = y + height;

                grid[deltaY][deltaX] = null;
            }
        }
    }

    private void mergeX(Chunk chunk, Direction direction, int width, int height, int depth) {
        Vector3i chunkPosition = chunk.getPosition();

        for (int x = 0; x < width; x++) {
            BlockType[][] grid = this.getGridX(chunk, direction, x, depth, height);

            for (int y = 0; y < height; y++) {
                for (int z = 0; z < depth; z++) {
                    BlockType blockType = grid[y][z];

                    if (blockType == null) {
                        continue;
                    }

                    int maximumHeight = this.findMaximumHeight(grid, z, y, height);
                    int maximumWidth = this.findMaximumWidth(grid, z, y, depth, maximumHeight);

                    Vector3i position = new Vector3i(x, y, z).add(chunkPosition);

                    this.addQuad(position, direction, blockType, 1, maximumHeight, maximumWidth);

                    this.resetGrid(grid, z, y, maximumWidth, maximumHeight);
                }
            }
        }
    }

    private void mergeZ(Chunk chunk, Direction direction, int width, int height, int depth) {
        Vector3i chunkPosition = chunk.getPosition();

        for (int z = 0; z < depth; z++) {
            BlockType[][] grid = this.getGridZ(chunk, direction, z, width, height);

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    BlockType blockType = grid[y][x];

                    if (blockType == null) {
                        continue;
                    }

                    int maximumHeight = this.findMaximumHeight(grid, x, y, height);
                    int maximumWidth = this.findMaximumWidth(grid, x, y, width, maximumHeight);

                    Vector3i position = new Vector3i(x, y, z).add(chunkPosition);

                    this.addQuad(position, direction, blockType, maximumWidth, maximumHeight, 1);

                    this.resetGrid(grid, x, y, maximumWidth, maximumHeight);
                }
            }
        }
    }

    private void mergeY(Chunk chunk, Direction direction, int width, int height, int depth) {
        Vector3i chunkPosition = chunk.getPosition();

        for (int y = 0; y < height; y++) {
            BlockType[][] grid = this.getGridY(chunk, direction, y, width, depth);

            for (int z = 0; z < depth; z++) {
                for (int x = 0; x < width; x++) {
                    BlockType blockType = grid[z][x];

                    if (blockType == null) {
                        continue;
                    }

                    int maximumHeight = this.findMaximumHeight(grid, x, z, depth);
                    int maximumWidth = this.findMaximumWidth(grid, x, z, width, maximumHeight);

                    Vector3i position = new Vector3i(x, y, z).add(chunkPosition);

                    this.addQuad(position, direction, blockType, maximumWidth, 1, maximumHeight);

                    this.resetGrid(grid, x, z, maximumWidth, maximumHeight);
                }
            }
        }
    }

    private BlockType[][] getGridX(Chunk chunk, Direction direction, int x, int depth, int height) {
        BlockType grid[][] = new BlockType[height][depth];

        for (int y = 0; y < height; y++) {
            for (int z = 0; z < depth; z++) {
                if (chunk.isBlockActive(x, y, z)) {
                    Block block = chunk.getBlock(x, y, z);

                    if (!block.isFaceActive(direction)) {
                        continue;
                    }

                    grid[y][z] = block.getBlockType();
                }
            }
        }

        return grid;
    }

    private BlockType[][] getGridY(Chunk chunk, Direction direction, int y, int width, int depth) {
        BlockType grid[][] = new BlockType[depth][width];

        for (int z = 0; z < depth; z++) {
            for (int x = 0; x < width; x++) {
                if (chunk.isBlockActive(x, y, z)) {
                    Block block = chunk.getBlock(x, y, z);

                    if (!block.isFaceActive(direction)) {
                        continue;
                    }

                    grid[z][x] = block.getBlockType();
                }
            }
        }

        return grid;
    }

    private BlockType[][] getGridZ(Chunk chunk, Direction direction, int z, int width, int height) {
        BlockType grid[][] = new BlockType[height][width];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (chunk.isBlockActive(x, y, z)) {
                    Block block = chunk.getBlock(x, y, z);

                    if (!block.isFaceActive(direction)) {
                        continue;
                    }

                    grid[y][x] = block.getBlockType();
                }
            }
        }

        return grid;
    }

    private void merge(Chunk chunk) {
        int width = chunk.getWidth();
        int depth = chunk.getDepth();
        int height = chunk.getHeight();

        this.quads.clear();

        for (Direction direction : Direction.values()) {
            if (direction == Direction.TOP || direction == Direction.BOTTOM) {
                this.mergeY(chunk, direction, width, height, depth);
            } else if (direction == Direction.LEFT || direction == Direction.RIGHT) {
                this.mergeX(chunk, direction, width, height, depth);
            } else {
                this.mergeZ(chunk, direction, width, height, depth);
            }
        }
    }

    public void addBlock(Block block) {
        Vector3i position = block.getGlobalPosition();

        BlockType blockType = block.getBlockType();

        for (Direction direction : Direction.values()) {
            if (block.isFaceActive(direction)) {
                this.addQuad(position, direction, blockType);
            }
        }
    }

    public void addQuad(Vector3i position, Direction direction, BlockType blockType, int width, int height, int depth) {
        Quad quad = addQuad(position, direction, blockType);

        quad.scale(direction, width, height, depth);

        quad.translate(position);
    }

    public Quad addQuad(Vector3i position, Direction direction, BlockType blockType) {
        int textureId = blockType.getIndex() * 6 + direction.getIndex();

        Texture texture = new Texture(direction, textureId);

        Quad quad = new Quad(position, direction, texture);

        this.quads.add(quad);

        return quad;
    }

    public void build() {
        int[] indices = this.getMergedIndices();
        int[] textures = this.getMergedTextures();

        float[] vertices = this.getMergedVertices();
        float[] coordinates = this.getMergedCoordinates();

        BufferData bufferData = new BufferData(vertices, indices, coordinates, textures);

        this.buffer.cleanup();

        this.buffer.setup(bufferData);
    }

    private int[] getMergedIndices() {
        int offset = 0;

        int[] indices;

        this.indices.clear();

        for (Quad quad : this.quads) {
            for (int index : quad.getIndices()) {
                this.indices.add(index + offset);
            }

            offset += 4;
        }

        indices = new int[this.indices.size()];

        for (int i = 0; i < this.indices.size(); i++) {
            indices[i] = this.indices.get(i);
        }

        return indices;
    }

    private float[] getMergedVertices() {
        float[] vertices;

        this.vertices.clear();

        for (Quad quad : this.quads) {
            for (float vertex : quad.getVertices()) {
                this.vertices.add(vertex);
            }
        }

        vertices = new float[this.vertices.size()];

        for (int i = 0; i < this.vertices.size(); i++) {
            vertices[i] = this.vertices.get(i);
        }

        return vertices;
    }

    private float[] getMergedCoordinates() {
        float[] coordinates;

        this.coordinates.clear();

        for (Quad quad : this.quads) {
            Texture texture = quad.getTexture();

            for (float coordinate : texture.getCoordinates()) {
                this.coordinates.add(coordinate);
            }
        }

        coordinates = new float[this.coordinates.size()];

        for (int i = 0; i < this.coordinates.size(); i++) {
            coordinates[i] = this.coordinates.get(i);
        }

        return coordinates;
    }

    private int[] getMergedTextures() {
        int[] textures;

        this.textures.clear();

        for (Quad quad : this.quads) {
            Texture texture = quad.getTexture();

            for (int i = 0; i < 4; i++) {
                this.textures.add(texture.getId());
            }
        }

        textures = new int[this.textures.size()];

        for (int i = 0; i < this.textures.size(); i++) {
            textures[i] = this.textures.get(i);
        }

        return textures;
    }

    public void cleanup() {
        this.buffer.cleanup();
    }

    public void render() {
        this.buffer.bindVertexArray();

        glDrawElements(GL_TRIANGLES, this.indices.size(), GL_UNSIGNED_INT, 0);

        this.buffer.detachVertexArray();
    }
}
