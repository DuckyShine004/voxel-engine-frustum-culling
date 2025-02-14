package com.duckyshine.app.asset;

import org.json.JSONArray;
import org.json.JSONObject;

import com.duckyshine.app.utility.FileUtility;

public class AssetLoader {
    public static void loadShaders() {
        JSONObject jsonObject = FileUtility.getFileToJSONObject("assets.json");

        JSONArray shaders = jsonObject.getJSONArray("shaders");

        for (int i = 0; i < shaders.length(); i++) {
            JSONObject shader = shaders.getJSONObject(i);

            String key = shader.getString("key");
            String vertexShaderPath = shader.getString("vertexShaderPath");
            String fragmentShaderPath = shader.getString("fragmentShaderPath");

            AssetPool.addShader(key, vertexShaderPath, fragmentShaderPath);
        }
    }
}
