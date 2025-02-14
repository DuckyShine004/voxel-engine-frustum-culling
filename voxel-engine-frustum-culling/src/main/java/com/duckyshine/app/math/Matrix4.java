package com.duckyshine.app.math;

import org.joml.Matrix4f;

public class Matrix4 {
    public static Matrix4f mul(Matrix4f u, Matrix4f v) {
        return u.mul(v, new Matrix4f());
    }
}
