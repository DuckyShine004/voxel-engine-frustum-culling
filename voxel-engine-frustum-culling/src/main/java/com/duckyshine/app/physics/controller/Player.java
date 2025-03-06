package com.duckyshine.app.physics.controller;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;

import com.duckyshine.app.math.Vector3;
import com.duckyshine.app.model.Block;
import com.duckyshine.app.model.BlockType;
import com.duckyshine.app.camera.Camera;

import com.duckyshine.app.physics.AABB;
import com.duckyshine.app.physics.ray.Ray;
import com.duckyshine.app.physics.ray.RayResult;
import com.duckyshine.app.scene.ChunkManager;
import com.duckyshine.app.scene.Scene;
import com.duckyshine.app.debug.Debug;

import static org.lwjgl.glfw.GLFW.*;

public class Player {
    private final int RENDER_DISTANCE = 2;

    private final float SPEED = 10.0f;
    private final float VERTICAL_SPEED = 5.0f; // I think 5 works fine, that should be ~ vertical speed as og mc

    private final float GRAVITY = 9.81f; // True: 31.36

    private final float WIDTH = 0.8f;
    private final float DEPTH = 0.8f;
    private final float HEIGHT = 1.8f;

    private final float RAY_DISTANCE = 8.0f;

    private final float CAMERA_OFFSET_X = 1.0f;
    private final float CAMERA_OFFSET_Y = 2.0f;
    private final float CAMERA_OFFSET_Z = -2.0f;

    private boolean isGrounded;
    private boolean isGravityOn;

    private boolean isLeftMouseButtonClicked;
    private boolean isRightMouseButtonClicked;

    private Vector3f position;
    private Vector3f velocity;

    private Vector3f dimension;

    private AABB aabb;

    private Camera camera;

    public Player() {
        this.position = new Vector3f();

        this.initialise();
    }

    public Player(float x, float y, float z) {
        this.position = new Vector3f(x, y, z);

        this.initialise();
    }

    public Player(Vector3f position) {
        this.position = position;

        this.initialise();
    }

    private void initialise() {
        this.isGravityOn = true;

        this.camera = new Camera(this.getCameraPosition());

        this.dimension = new Vector3f(this.WIDTH, this.HEIGHT, this.DEPTH);

        this.velocity = new Vector3f();

        this.aabb = new AABB(
                this.position.x - this.WIDTH / 2.0f,
                this.position.y,
                this.position.z - this.DEPTH / 2.0f,
                this.position.x + this.WIDTH / 2.0f,
                this.position.y + this.HEIGHT,
                this.position.z + this.DEPTH / 2.0f);
    }

    public void setIsGrounded(boolean isGrounded) {
        this.isGrounded = isGrounded;

        if (isGrounded) {
            this.resetVerticalVelocity();
        }
    }

    public void resetVerticalVelocity() {
        this.velocity.y = 0.0f;
    }

    public boolean getIsGrounded() {
        return this.isGrounded;
    }

    private Vector3f getCameraPosition() {
        Vector3f cameraPosition = new Vector3f(this.position);

        cameraPosition.x += this.CAMERA_OFFSET_X;
        cameraPosition.y += this.CAMERA_OFFSET_Y;
        cameraPosition.z += this.CAMERA_OFFSET_Z;

        return cameraPosition;
    }

    public void updateAABB() {
        this.updateAABBMin();
        this.updateAABBMax();
    }

    public void updateHorizontalVelocity(long window, float deltaTime) {
        Vector3f front = this.camera.getFront();
        Vector3f right = this.camera.getRight();

        Vector3f velocity = new Vector3f();

        // another issue is that forward and back vel are reliant on camera vector
        if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) {
            velocity.add(front);
        }

        if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) {
            velocity.sub(front);
        }

        if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) {
            velocity.add(right);
        }

        if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS) {
            velocity.sub(right);
        }

        if (velocity.length() != 0.0f) {
            velocity.normalize().mul(this.SPEED);
        }

        this.velocity.x = velocity.x;
        this.velocity.z = velocity.z;
    }

    public void updateVerticalVelocity(long window, float deltaTime) {
        if (glfwGetKey(window, GLFW_KEY_G) == GLFW_PRESS) {
            this.isGravityOn ^= true;
        }

        if (glfwGetKey(window, GLFW_KEY_SPACE) == GLFW_PRESS && (this.isGrounded || !this.isGravityOn)) {
            this.velocity.y = this.VERTICAL_SPEED;
        }

        if (glfwGetKey(window, GLFW_KEY_LEFT_SHIFT) == GLFW_PRESS && !this.isGravityOn) {
            this.velocity.y = -this.VERTICAL_SPEED;
        }

        if (this.isGravityOn) {
            this.velocity.y -= GRAVITY * deltaTime;
        }
    }

    public void updateVelocity(long window, float deltaTime) {
        this.updateHorizontalVelocity(window, deltaTime);
        this.updateVerticalVelocity(window, deltaTime);
    }

    public Vector3f getNextPosition(float deltaTime) {
        Vector3f position = new Vector3f(this.position);

        position.add(Vector3.mul(deltaTime, this.velocity));

        if (!this.isGravityOn) {
            this.resetVerticalVelocity();
        }

        return position;
    }

    public void setPosition(Vector3f position) {
        this.position.set(position);

        this.updateAABB();

        this.camera.setPosition(this.getCameraPosition());

        this.camera.updateMatrices();
    }

    public void update(long window, Scene scene) {
        this.addBlock(window, scene);
        this.removeBlock(window, scene);
    }

    public void addBlock(long window, Scene scene) {
        int clickState = glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_RIGHT);

        if (clickState == GLFW_PRESS && !this.isRightMouseButtonClicked) {
            this.addBlock(scene);

            this.isRightMouseButtonClicked = true;
        }

        if (clickState == GLFW_RELEASE && this.isRightMouseButtonClicked) {
            this.isRightMouseButtonClicked = false;
        }
    }

    public void addBlock(Scene scene) {
        Vector3f origin = this.camera.getPosition();
        Vector3f direction = this.camera.getFront();

        Ray ray = new Ray(origin, direction, this.RAY_DISTANCE);

        RayResult rayResult = ray.cast(scene);

        ChunkManager chunkManager = scene.getChunkManager();

        if (rayResult.getIsIntersect()) {
            chunkManager.addBlock(rayResult);
        }
    }

    public void removeBlock(long window, Scene scene) {
        int clickState = glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_LEFT);

        if (clickState == GLFW_PRESS && !this.isLeftMouseButtonClicked) {
            this.removeBlock(scene);

            this.isLeftMouseButtonClicked = true;
        }

        if (clickState == GLFW_RELEASE && this.isLeftMouseButtonClicked) {
            this.isLeftMouseButtonClicked = false;
        }
    }

    public void removeBlock(Scene scene) {
        Vector3f origin = this.camera.getPosition();
        Vector3f direction = this.camera.getFront();

        Ray ray = new Ray(origin, direction, this.RAY_DISTANCE);

        RayResult rayResult = ray.cast(scene);

        ChunkManager chunkManager = scene.getChunkManager();

        if (rayResult.getIsIntersect()) {
            chunkManager.removeBlock(rayResult.getPosition());
        }
    }

    public void updateAABBMin() {
        Vector3f min = this.aabb.getMin();

        min.x = this.position.x - this.dimension.x / 2.0f;
        min.y = this.position.y;
        min.z = this.position.z - this.dimension.z / 2.0f;
    }

    public void updateAABBMax() {
        Vector3f max = this.aabb.getMax();

        max.x = this.position.x + this.dimension.x / 2.0f;
        max.y = this.position.y + this.dimension.y;
        max.z = this.position.z + this.dimension.z / 2.0f;
    }

    public Camera getCamera() {
        return this.camera;
    }

    public AABB getAABB() {
        return this.aabb;
    }

    public Vector3f getPosition() {
        return this.position;
    }

    public float getHeight() {
        return this.HEIGHT;
    }

    public boolean isGravityOn() {
        return this.isGravityOn;
    }

    public Vector3f getVelocity() {
        return this.velocity;
    }

    public int getRenderDistance() {
        return this.RENDER_DISTANCE;
    }
}
