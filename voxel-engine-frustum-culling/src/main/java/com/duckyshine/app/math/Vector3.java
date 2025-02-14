package com.duckyshine.app.math;

import org.joml.Vector3f;

public class Vector3 {
    public static Vector3f add(Vector3f u, Vector3f v) {
        return new Vector3f(u.x + v.x, u.y + v.y, u.z + v.z);
    }

    public static Vector3f sub(Vector3f u, Vector3f v) {
        return new Vector3f(u.x - v.x, u.y - v.y, u.z - v.z);
    }

    public static Vector3f mul(float t, Vector3f u) {
        return new Vector3f(t * u.x, t * u.y, t * u.z);
    }

    public static Vector3f cross(Vector3f u, Vector3f v) {
        float dx = u.y * v.z - u.z * v.y;
        float dy = u.z * v.x - u.x * v.z;
        float dz = u.x * v.y - u.y * v.x;

        return new Vector3f(dx, dy, dz);
    }

    public static Vector3f normalize(Vector3f u) {
        float magnitude = u.length();

        if (magnitude == 0.0f) {
            return u;
        }

        return new Vector3f(u.x / magnitude, u.y / magnitude, u.z / magnitude);
    }
}
