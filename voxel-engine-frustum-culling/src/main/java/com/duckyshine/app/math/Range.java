package com.duckyshine.app.math;

import org.joml.Vector3f;
import org.joml.Vector3i;

// Exclusive Range IMPORTANT, i.e., [a,b)
public class Range {
    public static boolean isInRange1D(int value, int lower, int upper) {
        return value >= lower && value < upper;
    }

    public static boolean isInRange3D(Vector3i position, int width, int height, int depth) {
        if (!Range.isInRange1D(position.x, 0, width)) {
            return false;
        }

        if (!Range.isInRange1D(position.y, 0, height)) {
            return false;
        }

        return Range.isInRange1D(position.z, 0, depth);
    }
}
