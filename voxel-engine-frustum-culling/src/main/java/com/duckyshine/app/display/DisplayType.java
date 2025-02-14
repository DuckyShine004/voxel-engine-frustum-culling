package com.duckyshine.app.display;

import org.joml.Vector2i;

public enum DisplayType {
    SVGA(800, 600),
    XGA(1024, 768),
    DEFAULT(XGA);

    private final int width;
    private final int height;

    private final Vector2i resolution;

    private DisplayType(DisplayType displayType) {
        this.width = displayType.width;
        this.height = displayType.height;

        this.resolution = displayType.resolution;
    }

    private DisplayType(int width, int height) {
        this.width = width;
        this.height = height;

        this.resolution = new Vector2i(width, height);
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public Vector2i getResolution() {
        return this.resolution;
    }
}
