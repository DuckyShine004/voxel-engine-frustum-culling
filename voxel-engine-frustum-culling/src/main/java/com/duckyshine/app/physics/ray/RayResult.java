package com.duckyshine.app.physics.ray;

import org.joml.Vector3f;

public class RayResult {
    private boolean isIntersect;

    private Vector3f axes;
    private Vector3f position;

    public RayResult() {
        this.axes = new Vector3f();
        this.position = null;
    }

    public RayResult(Vector3f position, Vector3f axes) {
        this.axes = axes;
        this.position = position;
    }

    public void setAxes(Vector3f axes) {
        this.axes = axes;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public void setIsIntersect(boolean isIntersect) {
        this.isIntersect = isIntersect;
    }

    public boolean getIsIntersect() {
        return this.isIntersect;
    }

    public Vector3f getAxes() {
        return this.axes;
    }

    public Vector3f getPosition() {
        return this.position;
    }
}
