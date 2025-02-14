package com.duckyshine.app.physics;

import static org.lwjgl.opengl.GL11.*;

import org.joml.Vector3f;

import com.duckyshine.app.buffer.AABBBuffer;
import com.duckyshine.app.buffer.Buffer;
import com.duckyshine.app.buffer.BufferData;
import com.duckyshine.app.math.Axis;

public class AABB {
    private final int[] INDICES = {
            0, 1, 1, 2, 2, 3, 3, 0,
            4, 5, 5, 6, 6, 7, 7, 4,
            0, 4, 1, 5, 2, 6, 3, 7
    };

    private Vector3f min;
    private Vector3f max;

    private Buffer aabbBuffer;

    public AABB(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        this.min = new Vector3f(minX, minY, minZ);
        this.max = new Vector3f(maxX, maxY, maxZ);

        this.aabbBuffer = new AABBBuffer();
    }

    public AABB(Vector3f min, Vector3f max) {
        this.min = min;
        this.max = max;

        this.aabbBuffer = new AABBBuffer();
    }

    public float[] getVertices() {
        float[] vertices = {
                this.min.x, this.min.y, this.min.z,
                this.max.x, this.min.y, this.min.z,
                this.max.x, this.min.y, this.max.z,
                this.min.x, this.min.y, this.max.z,
                this.min.x, this.max.y, this.min.z,
                this.max.x, this.max.y, this.min.z,
                this.max.x, this.max.y, this.max.z,
                this.min.x, this.max.y, this.max.z
        };

        return vertices;
    }

    public AABB getOffset(float offset, Axis axis) {
        switch (axis) {
            case X:
                return new AABB(
                        this.min.x + offset,
                        this.min.y,
                        this.min.z,
                        this.max.x + offset,
                        this.max.y,
                        this.max.z);
            case Y:
                return new AABB(
                        this.min.x,
                        this.min.y + offset,
                        this.min.z,
                        this.max.x,
                        this.max.y + offset,
                        this.max.z);
            case Z:
                return new AABB(
                        this.min.x,
                        this.min.y,
                        this.min.z + offset,
                        this.max.x,
                        this.max.y,
                        this.max.z + offset);
            default:
                return this;
        }
    }

    public float getCentre(Axis axis) {
        switch (axis) {
            case X:
                return (this.min.x + this.max.x) / 2.0f;
            case Y:
                return (this.min.y + this.max.y) / 2.0f;
            case Z:
                return (this.min.z + this.max.z) / 2.0f;
            default:
                return 0.0f;
        }
    }

    public void loadBuffer() {
        BufferData bufferData = new BufferData(this.getVertices(), this.INDICES);

        this.aabbBuffer.setup(bufferData);
    }

    public void render() {
        glDrawElements(GL_LINES, this.INDICES.length, GL_UNSIGNED_INT, 0);

        this.aabbBuffer.detachVertexArray();
    }

    public Vector3f getMin() {
        return this.min;
    }

    public Vector3f getMax() {
        return this.max;
    }
}
