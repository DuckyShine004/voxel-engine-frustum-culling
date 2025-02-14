package com.duckyshine.app.buffer;

import static org.lwjgl.opengl.GL30.*;

public class MeshBuffer extends Buffer {
    private int textureBufferId;
    private int coordinateBufferId;

    public MeshBuffer() {
        super();

        this.textureBufferId = 0;
        this.coordinateBufferId = 0;
    }

    @Override
    public void setup(BufferData bufferData) {
        super.setupBuffers();

        this.coordinateBufferId = glGenBuffers();
        this.textureBufferId = glGenBuffers();

        this.bindVertexArray();

        this.bindVertexBuffer(bufferData.getVertices());
        this.setVertexAttributePointer(0, 3, GL_FLOAT, 3 * Float.BYTES, 0);

        this.bindCoordinateBuffer(bufferData.getCoordinates());
        this.setVertexAttributePointer(1, 2, GL_FLOAT, 2 * Float.BYTES, 0);

        this.bindTextureBuffer(bufferData.getTextures());
        this.setIntegerVertexAttributePointer(2, 1, GL_INT, Integer.BYTES, 0);

        this.bindIndexBuffer(bufferData.getIndices());

        this.detachVertexArray();
    }

    private void bindCoordinateBuffer(float[] coordinates) {
        this.bindFloatBuffer(this.coordinateBufferId, coordinates);
    }

    private void detachCoordinateBuffer() {
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    private void deleteCoordinateBuffer() {
        if (this.coordinateBufferId != 0) {
            this.detachCoordinateBuffer();

            glDeleteBuffers(this.coordinateBufferId);

            this.coordinateBufferId = 0;
        }
    }

    private void bindTextureBuffer(int[] textures) {
        this.bindIntegerBuffer(this.textureBufferId, textures, GL_ARRAY_BUFFER);
    }

    private void detachTextureBuffer() {
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    private void deleteTextureBuffer() {
        if (this.textureBufferId != 0) {
            this.detachTextureBuffer();

            glDeleteBuffers(this.textureBufferId);

            this.textureBufferId = 0;
        }
    }

    @Override
    public void cleanup() {
        super.cleanup();

        this.deleteCoordinateBuffer();
        this.deleteTextureBuffer();
    }
}
