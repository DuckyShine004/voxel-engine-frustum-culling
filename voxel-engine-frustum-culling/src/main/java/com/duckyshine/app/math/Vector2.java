package com.duckyshine.app.math;

import org.joml.Vector2i;
import org.joml.Vector3i;

public class Vector2 {
    public static Vector2i getXZInteger(Vector3i u) {
        return new Vector2i(u.x, u.z);
    }
}
