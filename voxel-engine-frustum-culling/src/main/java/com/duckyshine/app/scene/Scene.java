package com.duckyshine.app.scene;

import java.util.Map;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;

import org.joml.Vector3f;
import org.joml.Vector3i;

import com.duckyshine.app.physics.AABB;

import com.duckyshine.app.physics.controller.Player;
import com.duckyshine.app.physics.ray.RayResult;
import com.duckyshine.app.asset.AssetPool;

import com.duckyshine.app.camera.Camera;

import com.duckyshine.app.math.Axis;
import com.duckyshine.app.math.Vector3;
import com.duckyshine.app.math.Voxel;
import com.duckyshine.app.model.Mesh;
import com.duckyshine.app.model.Block;
import com.duckyshine.app.model.BlockType;
import com.duckyshine.app.model.Chunk;

import com.duckyshine.app.shader.Shader;
import com.duckyshine.app.shader.ShaderType;

import com.duckyshine.app.debug.Debug;

// Only two places where I need to change the constants, here and Chunk
public class Scene {
    private Shader shader;

    private Player player;

    private ChunkManager chunkManager;

    public Scene() {
        this.player = new Player(0.0f, 20.0f, 0.0f);

        this.shader = AssetPool.getShader(ShaderType.WORLD.getName());

        this.chunkManager = new ChunkManager();
    }

    public Scene(Shader shader) {
        this.player = new Player();

        this.shader = shader;

        this.chunkManager = new ChunkManager();
    }

    public void initialise() {
        this.chunkManager.initialise();
    }

    public boolean isColliding(AABB aabb) {
        Vector3f min = aabb.getMin();
        Vector3f max = aabb.getMax();

        int minX = (int) Math.floor(min.x);
        int minY = (int) Math.floor(min.y);
        int minZ = (int) Math.floor(min.z);

        int maxX = (int) Math.floor(max.x);
        int maxY = (int) Math.floor(max.y);
        int maxZ = (int) Math.floor(max.z);

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Vector3f position = new Vector3f(x, y, z);

                    if (this.chunkManager.isBlockActiveAtGlobalPosition(position)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public void checkCollisions(long window, float deltaTime) {
        this.player.updateVelocity(window, deltaTime);

        Vector3f position = this.player.getPosition();
        Vector3f target = this.player.getNextPosition(deltaTime);

        target.x = this.getAxisCollision(position.x, target.x, Axis.X);
        target.y = this.getAxisCollision(position.y, target.y, Axis.Y);
        target.z = this.getAxisCollision(position.z, target.z, Axis.Z);

        this.player.setPosition(target);
    }

    // Refactor to controller for other dynamic entities
    private void setControllerGravity(Player player, Axis axis, float position, float target, boolean isColliding) {

    }

    public float getAxisCollision(float position, float target, Axis axis) {
        AABB aabb = this.player.getAABB();

        AABB offsetAABB = aabb.getOffset(target - position, axis);

        boolean isColliding = this.isColliding(offsetAABB);

        if (axis == Axis.Y) {
            if (isColliding) {
                if (target <= position) {
                    this.player.setIsGrounded(true);
                } else {
                    this.player.resetVerticalVelocity();
                }
            } else {
                this.player.setIsGrounded(false);
            }
        }

        return isColliding ? position : target;
    }

    public void update(long window, float deltaTime) {
        this.checkCollisions(window, deltaTime);

        this.player.update(window, this);

        this.chunkManager.update(this.player);
    }

    public void setShader(ShaderType shaderType) {
        Camera camera = this.player.getCamera();

        this.shader = AssetPool.getShader(shaderType.getName());

        this.shader.use();

        this.shader.setMatrix4f("projectionViewMatrix", camera.getProjectionView());
    }

    public void render() {
        this.setShader(ShaderType.WORLD);

        this.chunkManager.render();

        AABB aabb = this.player.getAABB();

        aabb.loadBuffer();
        this.setShader(ShaderType.AABB);
        aabb.render();
    }

    public void cleanup() {
        this.chunkManager.cleanup();
    }

    public Camera getCamera() {
        return this.player.getCamera();
    }

    public ChunkManager getChunkManager() {
        return this.chunkManager;
    }
}
