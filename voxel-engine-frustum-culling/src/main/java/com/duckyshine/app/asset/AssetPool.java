package com.duckyshine.app.asset;

import java.util.Map;
import java.util.HashMap;

import com.duckyshine.app.shader.Shader;

import com.duckyshine.app.utility.ResourceFinder;

import com.duckyshine.app.debug.Debug;

public class AssetPool {
    private static Map<String, Shader> shaders = new HashMap<>();

    public static Shader getShader(String key) {
        return AssetPool.shaders.containsKey(key) ? AssetPool.shaders.get(key) : null;
    }

    public static void addShader(String key, String vertexShaderPath, String fragmentShaderPath) {
        String vertexShaderFile = ResourceFinder.getFile(vertexShaderPath);
        String fragmentShaderFile = ResourceFinder.getFile(fragmentShaderPath);

        Shader shader = new Shader(vertexShaderFile, fragmentShaderFile);

        AssetPool.shaders.put(key, shader);
    }
}
