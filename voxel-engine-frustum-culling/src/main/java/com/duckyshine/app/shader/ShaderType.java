package com.duckyshine.app.shader;

public enum ShaderType {
    WORLD,
    AABB;

    public String getName() {
        return this.name().toLowerCase();
    }
}
