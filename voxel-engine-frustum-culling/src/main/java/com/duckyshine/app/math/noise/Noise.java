package com.duckyshine.app.math.noise;

import com.duckyshine.app.debug.Debug;
import com.duckyshine.app.math.RandomNumber;

public class Noise {
    private static final long SEED = RandomNumber.getRandomLong();

    private static final int OCTAVES = 3;

    private static final double PERSISTENCE = 0.5f;

    private static final double LACUNARITY = 2.0f;

    // Default: 1,10
    //
    // Use lower freq with higher scale:
    // Best settings:
    // 0.5,20
    private static final double FREQUENCY = 0.5f;

    private static final double SCALE = 20.0f;

    public static int getNoise2d(int x, int z) {
        return Noise.getNoise2d((double) x, (double) z);
    }

    public static int getNoise2d(double x, double z) {
        double totalNoise = 0.0f;
        double totalAmplitude = 0.0f;

        double amplitude = 1.0f;
        double frequency = Noise.FREQUENCY;

        for (int i = 0; i < Noise.OCTAVES; i++) {
            double noise = Noise.getSimplexNoise2d(x * frequency, z * frequency);

            totalNoise += noise * amplitude;

            totalAmplitude += amplitude;

            amplitude *= Noise.PERSISTENCE;

            frequency *= Noise.LACUNARITY;
        }

        double normalisedNoise = totalNoise / totalAmplitude;

        int y = (int) Math.ceil((normalisedNoise + 1.0f) * 0.5f * SCALE);

        return y;
    }

    private static double getSimplexNoise2d(double x, double z) {
        return SimplexNoise.noise2(Noise.SEED, x, z);
    }

    // Performance improves slightly, at the cost of worse terrain generation
    private static double getFastSimplexNoise2d(double x, double z) {
        return FastSimplexNoise.noise2(Noise.SEED, x, z);
    }
}
