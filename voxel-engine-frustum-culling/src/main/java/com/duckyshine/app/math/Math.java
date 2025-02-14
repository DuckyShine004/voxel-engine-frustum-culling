package com.duckyshine.app.math;

import org.joml.Vector3i;

public class Math {
    public static boolean isInRange1D(int value, int lower, int upper) {
        return value >= lower && value < upper;
    }

    public static boolean isInRange3D(Vector3i position, int width, int height, int depth) {
        if (!Math.isInRange1D(position.x, 0, width)) {
            return false;
        }

        if (!Math.isInRange1D(position.y, 0, height)) {
            return false;
        }

        return Math.isInRange1D(position.z, 0, depth);
    }
}
