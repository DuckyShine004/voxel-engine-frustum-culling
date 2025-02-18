package com.duckyshine.app.physics.ray;

import org.joml.Vector3f;

import com.duckyshine.app.scene.Scene;
import com.duckyshine.app.scene.ChunkManager;

import com.duckyshine.app.debug.Debug;

public class Ray {
    private float distance;

    private Vector3f origin;
    private Vector3f direction;

    public Ray(Vector3f origin, Vector3f direction, float distance) {
        this.distance = distance;

        this.origin = origin;
        this.direction = direction;
    }

    public RayResult cast(Scene scene) {
        Vector3f step = new Vector3f();
        Vector3f position = this.getBlockPosition();
        Vector3f tMax = new Vector3f();
        Vector3f tDelta = new Vector3f();

        if (this.direction.x > 0) {
            step.x = 1.0f;
            tMax.x = ((position.x + 1.0f) - this.origin.x) / this.direction.x;
            tDelta.x = 1.0f / this.direction.x;
        } else if (this.direction.x < 0) {
            step.x = -1.0f;
            tMax.x = (this.origin.x - position.x) / -this.direction.x;
            tDelta.x = 1.0f / -this.direction.x;
        } else {
            tMax.x = Float.MAX_VALUE;
            tDelta.x = Float.MAX_VALUE;
        }

        if (this.direction.y > 0) {
            step.y = 1.0f;
            tMax.y = ((position.y + 1.0f) - this.origin.y) / this.direction.y;
            tDelta.y = 1.0f / this.direction.y;
        } else if (this.direction.y < 0) {
            step.y = -1.0f;
            tMax.y = (this.origin.y - position.y) / -this.direction.y;
            tDelta.y = 1.0f / -this.direction.y;
        } else {
            tMax.y = Float.MAX_VALUE;
            tDelta.y = Float.MAX_VALUE;
        }

        if (this.direction.z > 0) {
            step.z = 1.0f;
            tMax.z = ((position.z + 1.0f) - this.origin.z) / this.direction.z;
            tDelta.z = 1.0f / this.direction.z;
        } else if (this.direction.z < 0) {
            step.z = -1.0f;
            tMax.z = (this.origin.z - position.z) / -this.direction.z;
            tDelta.z = 1.0f / -this.direction.z;
        } else {
            tMax.z = Float.MAX_VALUE;
            tDelta.z = Float.MAX_VALUE;
        }

        return this.getRayResult(scene, step, tMax, tDelta, position);
    }

    private RayResult getRayResult(Scene scene, Vector3f step, Vector3f tMax, Vector3f tDelta, Vector3f position) {
        RayResult rayResult = new RayResult();

        Vector3f axes = new Vector3f();

        ChunkManager chunkManager = scene.getChunkManager();

        float t = 0.0f;

        while (t <= this.distance) {
            if (chunkManager.isBlockActiveAtGlobalPosition(position)) {
                rayResult.setIsIntersect(true);
                rayResult.setPosition(position);

                break;
            }

            if (tMax.x < tMax.y && tMax.x < tMax.z) {
                position.x += step.x;
                t = tMax.x;
                tMax.x += tDelta.x;
                axes.zero();
                axes.x = -step.x;
            } else if (tMax.y < tMax.z) {
                position.y += step.y;
                t = tMax.y;
                tMax.y += tDelta.y;
                axes.zero();
                axes.y = -step.y;
            } else {
                position.z += step.z;
                t = tMax.z;
                tMax.z += tDelta.z;
                axes.zero();
                axes.z = -step.z;
            }
        }

        rayResult.setAxes(axes);

        return rayResult;
    }

    private Vector3f getBlockPosition() {
        int x = (int) Math.floor(this.origin.x);
        int y = (int) Math.floor(this.origin.y);
        int z = (int) Math.floor(this.origin.z);

        return new Vector3f(x, y, z);
    }

    public Vector3f getOrigin() {
        return this.origin;
    }

    public Vector3f getDirection() {
        return this.direction;
    }

    public float getDistance() {
        return this.distance;
    }
}
