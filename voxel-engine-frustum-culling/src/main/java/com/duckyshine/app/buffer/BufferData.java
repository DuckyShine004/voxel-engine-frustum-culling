package com.duckyshine.app.buffer;

public class BufferData {
    private int[] indices;
    private int[] textures;

    private float[] vertices;
    private float[] coordinates;

    public BufferData(float[] vertices, int[] indices, float[] coordinates, int[] textures) {
        this.indices = indices;
        this.textures = textures;

        this.vertices = vertices;
        this.coordinates = coordinates;
    }

    public BufferData(float[] vertices, int[] indices) {
        this.indices = indices;

        this.vertices = vertices;
    }

    public int[] getIndices() {
        return this.indices;
    }

    public int[] getTextures() {
        return this.textures;
    }

    public float[] getVertices() {
        return this.vertices;
    }

    public float[] getCoordinates() {
        return this.coordinates;
    }
}
