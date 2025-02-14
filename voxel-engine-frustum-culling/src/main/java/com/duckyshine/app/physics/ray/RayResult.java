package com.duckyshine.app.physics.ray;

import org.joml.Vector3i;

public class RayResult {
    private Vector3i axes;
    private Vector3i position;

    public RayResult() {
        this.axes = new Vector3i();
        this.position = null;
    }

    public RayResult(Vector3i position, Vector3i axes) {
        this.axes = axes;
        this.position = position;
    }

    public void setAxes(Vector3i axes) {
        this.axes = axes;
    }

    public void setPosition(Vector3i position) {
        this.position = position;
    }

    public Vector3i getAxes() {
        return this.axes;
    }

    public Vector3i getPosition() {
        return this.position;
    }
}
