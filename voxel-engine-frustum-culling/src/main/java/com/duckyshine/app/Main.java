package com.duckyshine.app;

import java.nio.*;

import org.lwjgl.glfw.*;
import org.lwjgl.system.*;

import com.duckyshine.app.camera.Camera;

import com.duckyshine.app.display.Display;

import com.duckyshine.app.model.texture.Atlas;

import com.duckyshine.app.scene.Scene;

import com.duckyshine.app.sound.SoundPlayer;

import com.duckyshine.app.asset.AssetLoader;

import com.duckyshine.app.debug.Debug;

import static org.lwjgl.glfw.GLFW.*;

import static org.lwjgl.opengl.GL.*;
import static org.lwjgl.opengl.GL11.*;

import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Main {
    private long window;

    private float lastTime;

    private Scene scene;

    private SoundPlayer soundPlayer;

    private void initialise() {
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialise GLFW");
        }

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

        this.initialiseWindow();

        this.initialiseCallbacks();

        glfwSetInputMode(this.window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
    }

    private void initialiseWindow() {
        Display display = Display.get();

        this.window = glfwCreateWindow(display.getWidth(), display.getHeight(), "Greedy Meshing", NULL, NULL);

        if (this.window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        this.centreWindow();

        glfwMakeContextCurrent(this.window);
    }

    private void initialiseCallbacks() {
        glfwSetKeyCallback(this.window, this::keyCallback);

        glfwSetCursorPosCallback(this.window, this::cursorPosCallback);

        glfwSetFramebufferSizeCallback(this.window, this::frameBufferSizeCallback);
    }

    private void centreWindow() {
        try (MemoryStack stack = stackPush()) {
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);

            glfwGetWindowSize(this.window, width, height);

            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            int x = (vidmode.width() - width.get(0)) >> 1;
            int y = (vidmode.height() - height.get(0)) >> 1;

            glfwSetWindowPos(this.window, x, y);
        }
    }

    private void initialiseSceneObjects() {
        this.scene = new Scene();

        this.soundPlayer = new SoundPlayer();
    }

    private void initialiseSceneRenderingParameters() {
        AssetLoader.loadShaders();

        Atlas.setup(false);

        // glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);

        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);

        this.scene.initialise();
    }

    private void run() {
        this.lastTime = 0.0f;

        createCapabilities();

        this.initialiseSceneObjects();

        this.initialiseSceneRenderingParameters();

        while (!glfwWindowShouldClose(this.window)) {
            this.update();
            this.render();

            glfwSwapBuffers(this.window);
            glfwPollEvents();
        }

        glfwDestroyWindow(this.window);
        glfwTerminate();
    }

    private void update() {
        float time = (float) glfwGetTime();

        float deltaTime = time - this.lastTime;

        this.lastTime = time;

        this.scene.update(this.window, deltaTime);

        this.soundPlayer.playMusic();
    }

    private void render() {
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        this.scene.render();
    }

    private void frameBufferSizeCallback(long window, int width, int height) {
        Camera camera = this.scene.getCamera();

        glViewport(0, 0, width, height);

        camera.updateAspectRatio(width, height);
    }

    private void keyCallback(long window, int key, int scanmode, int action, int mods) {
        if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS) {
            this.exit();
        }

        if (key == GLFW_KEY_E && action == GLFW_PRESS) {
            this.toggleCursorMode();
        }
    }

    private void toggleCursorMode() {
        int cursorMode = glfwGetInputMode(this.window, GLFW_CURSOR);

        if (cursorMode == GLFW_CURSOR_DISABLED) {
            glfwSetInputMode(this.window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
        } else {
            glfwSetInputMode(this.window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
        }
    }

    private void cursorPosCallback(long window, double mouseX, double mouseY) {
        Camera camera = this.scene.getCamera();

        camera.rotate(mouseX, mouseY);
    }

    private void exit() {
        this.scene.cleanup();

        this.soundPlayer.cleanup();

        glfwSetWindowShouldClose(window, true);
    }

    public static void main(String[] args) {
        Main main = new Main();

        main.initialise();
        main.run();
    }
}
