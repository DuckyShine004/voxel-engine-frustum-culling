package com.duckyshine.app.model.texture;

import java.util.Arrays;

import com.duckyshine.app.math.Direction;

public class Texture {
    // private final float[][] COORDINATES = {
    // {
    // 0.0f, 1.0f,
    // 0.0f, 2.0f / 3.0f,
    // 0.5f, 2.0f / 3.0f,
    // 0.5f, 1.0f
    // },
    // {
    // 0.5f, 1.0f,
    // 0.5f, 2.0f / 3.0f,
    // 1.0f, 2.0f / 3.0f,
    // 1.0f, 1.0f

    // },
    // {
    // 0.0f, 2.0f / 3.0f,
    // 0.0f, 1.0f / 3.0f,
    // 0.5f, 1.0f / 3.0f,
    // 0.5f, 2.0f / 3.0f
    // },
    // {
    // 0.5f, 2.0f / 3.0f,
    // 0.5f, 1.0f / 3.0f,
    // 1.0f, 1.0f / 3.0f,
    // 1.0f, 2.0f / 3.0f
    // },
    // {
    // 0.0f, 1.0f / 3.0f,
    // 0.0f, 0.0f,
    // 0.5f, 0.0f,
    // 0.5f, 1.0f / 3.0f

    // },
    // {
    // 0.5f, 1.0f / 3.0f,
    // 0.5f, 0.0f,
    // 1.0f, 0.0f,
    // 1.0f, 1.0f / 3.0f
    // }
    // };

    private final float[][] COORDINATES = {
            {
                    0.0f, 1.0f,
                    0.0f, 0.0f,
                    1.0f, 0.0f,
                    1.0f, 1.0f
            },
            {
                    0.0f, 1.0f,
                    0.0f, 0.0f,
                    1.0f, 0.0f,
                    1.0f, 1.0f
            },
            {
                    0.0f, 1.0f,
                    0.0f, 0.0f,
                    1.0f, 0.0f,
                    1.0f, 1.0f
            },
            {
                    0.0f, 1.0f,
                    0.0f, 0.0f,
                    1.0f, 0.0f,
                    1.0f, 1.0f
            },
            {
                    0.0f, 1.0f,
                    0.0f, 0.0f,
                    1.0f, 0.0f,
                    1.0f, 1.0f
            },
            {
                    0.0f, 1.0f,
                    0.0f, 0.0f,
                    1.0f, 0.0f,
                    1.0f, 1.0f
            }
    };

    private int id;

    private float[] coordinates;

    public Texture(Direction direction, int id) {
        this.id = id;

        this.coordinates = copyCoordinates(direction);
    }

    public void scale(Direction direction, int width, int height, int depth) {
        int scaleX;
        int scaleY;

        if (direction == Direction.TOP || direction == Direction.BOTTOM) {
            scaleX = width;
            scaleY = depth;
        } else if (direction == Direction.LEFT || direction == Direction.RIGHT) {
            scaleX = depth;
            scaleY = height;
        } else {
            scaleX = width;
            scaleY = height;
        }

        for (int i = 0; i < this.coordinates.length; i += 2) {
            this.coordinates[i] *= scaleX;
            this.coordinates[i + 1] *= scaleY;
        }
    }

    public float[] copyCoordinates(Direction direction) {
        coordinates = this.COORDINATES[direction.getIndex()];

        coordinates = Arrays.copyOf(coordinates, coordinates.length);

        for (int i = 1; i < coordinates.length; i += 2) {
            coordinates[i] = 1.0f - coordinates[i];
        }

        return coordinates;
    }

    public int getId() {
        return this.id;
    }

    public float[] getCoordinates() {
        return this.coordinates;
    }
}
