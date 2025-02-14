package com.duckyshine.app.model;

import java.util.Arrays;

import org.joml.Vector3i;

import com.duckyshine.app.math.Direction;

import com.duckyshine.app.model.texture.Texture;

import com.duckyshine.app.debug.Debug;

public class Quad {
    private final int[][] INDICES = {
            { 0, 1, 2, 2, 3, 0 },
            { 0, 1, 2, 2, 3, 0 },
            { 0, 1, 2, 2, 3, 0 },
            { 0, 1, 2, 2, 3, 0 },
            { 0, 1, 2, 2, 3, 0 },
            { 0, 1, 2, 2, 3, 0 }
    };

    private final float[][] VERTICES = {
            {
                    0.0f, 1.0f, 0.0f,
                    0.0f, 1.0f, 1.0f,
                    1.0f, 1.0f, 1.0f,
                    1.0f, 1.0f, 0.0f
            },
            {
                    0.0f, 0.0f, 1.0f,
                    0.0f, 0.0f, 0.0f,
                    1.0f, 0.0f, 0.0f,
                    1.0f, 0.0f, 1.0f
            },
            {
                    0.0f, 1.0f, 0.0f,
                    0.0f, 0.0f, 0.0f,
                    0.0f, 0.0f, 1.0f,
                    0.0f, 1.0f, 1.0f
            },
            {
                    1.0f, 1.0f, 1.0f,
                    1.0f, 0.0f, 1.0f,
                    1.0f, 0.0f, 0.0f,
                    1.0f, 1.0f, 0.0f
            },
            {
                    0.0f, 1.0f, 1.0f,
                    0.0f, 0.0f, 1.0f,
                    1.0f, 0.0f, 1.0f,
                    1.0f, 1.0f, 1.0f
            },
            {
                    1.0f, 1.0f, 0.0f,
                    1.0f, 0.0f, 0.0f,
                    0.0f, 0.0f, 0.0f,
                    0.0f, 1.0f, 0.0f
            },
    };

    private int[] indices;

    private float[] vertices;

    private Texture texture;

    private Direction direction;

    public Quad(Vector3i position, Direction direction, Texture texture) {
        this.direction = direction;

        this.indices = this.INDICES[direction.getIndex()];

        this.vertices = this.copyVertices();

        this.texture = texture;
    }

    public void translate(Vector3i delta) {
        for (int i = 0; i < this.vertices.length; i += 3) {
            this.vertices[i] += (float) delta.x;
            this.vertices[i + 1] += (float) delta.y;
            this.vertices[i + 2] += (float) delta.z;
        }
    }

    public void scale(Direction direction, int width, int height, int depth) {
        for (int i = 0; i < this.vertices.length; i += 3) {
            this.vertices[i] *= (float) width;
            this.vertices[i + 1] *= (float) height;
            this.vertices[i + 2] *= (float) depth;
        }

        this.texture.scale(direction, width, height, depth);
    }

    public float[] copyVertices() {
        float[] vertices = this.VERTICES[this.direction.getIndex()];

        return Arrays.copyOf(vertices, vertices.length);
    }

    public int[] getIndices() {
        return this.indices;
    }

    public float[] getVertices() {
        return this.vertices;
    }

    public Texture getTexture() {
        return this.texture;
    }
}
