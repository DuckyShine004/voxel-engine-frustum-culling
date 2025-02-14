package com.duckyshine.app.buffer;

import java.nio.IntBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

import static org.lwjgl.opengl.GL30.*;

public abstract class Buffer {
    protected int vertexArrayId;

    protected int indexBufferId;
    protected int vertexBufferId;

    public Buffer() {
        this.vertexArrayId = 0;

        this.indexBufferId = 0;
        this.vertexBufferId = 0;
    }

    protected void setupBuffers() {
        this.vertexArrayId = glGenVertexArrays();

        this.vertexBufferId = glGenBuffers();
        this.indexBufferId = glGenBuffers();
    }

    public void bindVertexArray() {
        glBindVertexArray(this.vertexArrayId);
    }

    public void detachVertexArray() {
        glBindVertexArray(0);
    }

    protected void deleteVertexArray() {
        if (this.vertexArrayId != 0) {
            this.detachVertexArray();

            glDeleteVertexArrays(this.vertexArrayId);

            this.vertexArrayId = 0;
        }
    }

    protected void bindVertexBuffer(float[] vertices) {
        this.bindFloatBuffer(this.vertexBufferId, vertices);
    }

    protected void detachVertexBuffer() {
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    protected void deleteVertexBuffer() {
        if (this.vertexBufferId != 0) {
            this.detachVertexBuffer();

            glDeleteBuffers(this.vertexBufferId);

            this.vertexBufferId = 0;
        }
    }

    protected void bindIndexBuffer(int[] indices) {
        this.bindIntegerBuffer(this.indexBufferId, indices, GL_ELEMENT_ARRAY_BUFFER);
    }

    protected void detachIndexBuffer() {
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    protected void deleteIndexBuffer() {
        if (this.indexBufferId != 0) {
            this.detachIndexBuffer();

            glDeleteBuffers(this.indexBufferId);

            this.indexBufferId = 0;
        }
    }

    protected void setVertexAttributePointer(int index, int size, int type, int stride, long pointer) {
        glVertexAttribPointer(index, size, type, false, stride, pointer);

        glEnableVertexAttribArray(index);
    }

    protected void setIntegerVertexAttributePointer(int index, int size, int type, int stride, long pointer) {
        glVertexAttribIPointer(index, size, type, stride, pointer);

        glEnableVertexAttribArray(index);
    }

    protected void bindIntegerBuffer(int bufferId, int[] array, int target) {
        IntBuffer buffer = BufferUtils.createIntBuffer(array.length);

        glBindBuffer(target, bufferId);

        buffer.put(array).flip();

        glBufferData(target, buffer, GL_STATIC_DRAW);
    }

    protected void bindFloatBuffer(int bufferId, float[] array) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(array.length);

        glBindBuffer(GL_ARRAY_BUFFER, bufferId);

        buffer.put(array).flip();

        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
    }

    public int getVertexArrayId() {
        return this.vertexArrayId;
    }

    public abstract void setup(BufferData bufferData);

    public void cleanup() {
        this.deleteVertexArray();

        this.deleteVertexBuffer();
        this.deleteIndexBuffer();
    }
}
