package com.duckyshine.app.utility;

import java.io.File;
import java.io.IOException;

import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.JSONObject;

import org.apache.commons.io.FileUtils;

public class FileUtility {
    public static String getFilename(String filepath) {
        return Paths.get(filepath).getFileName().toString();
    }

    public static String getFilepath(String filepath) {
        Path path = Paths.get(filepath).getParent();

        return (path != null) ? path.toString() : null;
    }

    public static String getFileToString(String filepath) {
        File file = new File(filepath);

        try {
            return FileUtils.readFileToString(file, "UTF-8");
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        return null;
    }

    public static void saveImage(Object object, String filename) {

    }

    public static JSONObject getFileToJSONObject(String filepath) {
        Path jsonPath = ResourceFinder.getResourcePath(filepath);

        String jsonContent = null;

        try {
            jsonContent = new String(Files.readAllBytes(jsonPath));
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        return new JSONObject(jsonContent);
    }
}
