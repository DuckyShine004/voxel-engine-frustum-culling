package com.duckyshine.app.utility;

import java.io.IOException;

import java.net.URI;
import java.net.URISyntaxException;

import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.List;
import java.util.Arrays;

import java.util.stream.Stream;
import java.util.stream.Collectors;

import com.duckyshine.app.debug.Debug;

public class ResourceFinder {
    public static List<String> getFiles(String directory) {
        Path filepath = ResourceFinder.getResourcePath(directory);

        List<String> filenames = ResourceFinder.getFilenames(filepath);

        if (filenames == null) {
            return null;
        }

        return getAbsolutePaths(filepath, filenames);
    }

    public static List<String> getFiles(String... paths) {
        String fullPath = ResourceFinder.getFullPath(paths);

        return ResourceFinder.getFiles(fullPath);
    }

    private static List<String> getFilenames(Path filepath) {
        try (Stream<Path> paths = Files.list(filepath)) {
            return paths.map(path -> path.getFileName().toString()).collect(Collectors.toList());
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        return null;
    }

    private static List<String> getAbsolutePaths(Path filepath, List<String> filenames) {
        return filenames.stream().map(filename -> filepath.resolve(filename).toString()).toList();
    }

    public static Path getResourcePath(String directory) {
        ClassLoader classLoader = ResourceFinder.class.getClassLoader();
        URI uri = null;

        try {
            uri = classLoader.getResource(directory).toURI();
        } catch (URISyntaxException exception) {
            exception.printStackTrace();

            return null;
        }

        return Paths.get(uri);
    }

    public static String getFile(String... paths) {
        String fullPath = ResourceFinder.getFullPath(paths);

        return ResourceFinder.getFile(fullPath);
    }

    public static String getFile(String fullPath) {
        Path resourcePath = ResourceFinder.getResourcePath(fullPath);

        return resourcePath.toString();
    }

    public static String getFullPath(String... paths) {
        Path path = Path.of(paths[0], Arrays.copyOfRange(paths, 1, paths.length));

        return path.toString();
    }
}
