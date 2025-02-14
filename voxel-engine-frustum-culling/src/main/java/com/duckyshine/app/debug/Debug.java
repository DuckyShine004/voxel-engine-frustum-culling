package com.duckyshine.app.debug;

import java.nio.file.Path;

import java.util.Arrays;

import org.joml.Vector3f;
import org.joml.Vector3i;

public class Debug {
    private static final String RED = "\u001B[31m";
    private static final String RESET = "\u001B[0m";

    private static <T> String toString(T object) {
        if (object == null) {
            return "null";
        }

        if (object instanceof Path) {
            return object.toString();
        }

        if (object instanceof Iterable<?>) {
            return Debug.toString((Iterable<?>) object);
        }

        if (object.getClass().isArray()) {
            return Debug.castObjectArrayToString(Object[].class, object);
        }

        if (object instanceof Vector3f) {
            Vector3f vector = (Vector3f) object;

            return "[" + vector.x + ", " + vector.y + ", " + vector.z + "]";
        }

        if (object instanceof Vector3i) {
            Vector3i vector = (Vector3i) object;

            return "[" + vector.x + ", " + vector.y + ", " + vector.z + "]";
        }

        return object.toString();
    }

    private static <T> String toString(Iterable<T> iterable) {
        StringBuilder stringBuilder = new StringBuilder("[");

        boolean first = true;

        for (T object : iterable) {
            if (first) {
                first = false;
            } else {
                stringBuilder.append(", ");
            }

            stringBuilder.append(Debug.toString(object));
        }

        stringBuilder.append("]");

        return stringBuilder.toString();
    }

    private static <T> String toString(T[] objects) {
        StringBuilder stringBuilder = new StringBuilder("[");

        boolean first = true;

        for (T object : objects) {
            if (first) {
                first = false;
            } else {
                stringBuilder.append(", ");
            }

            stringBuilder.append(Debug.toString(object));
        }

        stringBuilder.append("]");

        return stringBuilder.toString();
    }

    private static String getClassName(String className) {
        String[] splitClassName = className.split("[.]");

        return splitClassName[splitClassName.length - 1];
    }

    private static String getMethodName(StackTraceElement caller) {
        String className = Debug.getClassName(caller.getClassName());
        String methodName = caller.getMethodName();

        return className + "." + methodName;
    }

    private static <T> String castObjectArrayToString(Class<T[]> cast, Object object) {
        String objectArrayString = Arrays.toString((T[]) object);

        return "[" + objectArrayString.substring(1, objectArrayString.length() - 1) + "]";
    }

    public static void debug(Object... objects) {
        StackTraceElement caller = Thread.currentThread().getStackTrace()[2];

        String methodName = Debug.getMethodName(caller);

        int lineNumber = caller.getLineNumber();

        System.err.print(RED + methodName + ":" + lineNumber + " ");

        for (int i = 0; i < objects.length; i++) {
            if (i > 0) {
                System.err.print(", ");
            }

            System.err.print(Debug.toString(objects[i]));
        }

        System.err.print("\n" + RESET);
    }
}
