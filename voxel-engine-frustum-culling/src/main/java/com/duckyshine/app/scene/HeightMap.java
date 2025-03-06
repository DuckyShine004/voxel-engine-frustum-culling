package com.duckyshine.app.scene;

import org.joml.Vector3i;

import com.duckyshine.app.math.noise.Noise;

public class HeightMap {
    int width;
    int depth;

    int[][] heights;

    public HeightMap(int width, int depth) {
        this.width = width;
        this.depth = depth;

        this.heights = new int[depth][width];
    }

    public void generate(Vector3i chunkPosition) {
        for (int dz = 0; dz < this.depth; dz++) {
            for (int dx = 0; dx < this.width; dx++) {
                double offsetX = (double) (chunkPosition.x + dx) / this.width - 0.5d;
                double offsetZ = (double) (chunkPosition.z + dz) / this.depth - 0.5d;

                this.heights[dz][dx] = Noise.getNoise2d(offsetX, offsetZ);
            }
        }
    }

    public int[][] getHeights() {
        return this.heights;
    }
}
