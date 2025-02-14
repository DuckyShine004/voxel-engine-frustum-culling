package com.duckyshine.app.shader;

import org.lwjgl.BufferUtils;

import org.joml.Vector3f;
import org.joml.Matrix4f;

import java.nio.FloatBuffer;

import com.duckyshine.app.utility.FileUtility;

import com.duckyshine.app.debug.Debug;

import static org.lwjgl.opengl.GL20.*;

public class Shader {
    private int vertexShader;
    private int fragmentShader;

    private int program;

    private String vertexShaderSource;
    private String fragmentShaderSource;

    public Shader(String vertexShaderFile, String fragmentShaderFile) {
        this.vertexShaderSource = FileUtility.getFileToString(vertexShaderFile);
        this.fragmentShaderSource = FileUtility.getFileToString(fragmentShaderFile);

        this.createAndCompileShaders();
        this.attachShadersAndLinkProgram();
    }

    private void checkShaderCompilationStatus(int shader) {
        int status = glGetShaderi(shader, GL_COMPILE_STATUS);

        if (status == GL_FALSE) {
            Debug.debug(glGetShaderInfoLog(shader));
        }
    }

    private void checkProgramLinkingStatus() {
        int status = glGetProgrami(this.program, GL_LINK_STATUS);

        if (status == GL_FALSE) {
            Debug.debug(glGetProgramInfoLog(this.program));
        }
    }

    private void createAndCompileShaders() {
        this.vertexShader = glCreateShader(GL_VERTEX_SHADER);
        this.fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);

        glShaderSource(this.vertexShader, this.vertexShaderSource);
        glShaderSource(this.fragmentShader, this.fragmentShaderSource);

        glCompileShader(this.vertexShader);
        this.checkShaderCompilationStatus(this.vertexShader);

        glCompileShader(this.fragmentShader);
        this.checkShaderCompilationStatus(this.fragmentShader);
    }

    private void attachShadersAndLinkProgram() {
        this.program = glCreateProgram();

        glAttachShader(this.program, this.vertexShader);
        glAttachShader(this.program, this.fragmentShader);

        glLinkProgram(this.program);
        this.checkProgramLinkingStatus();

        glDeleteShader(this.vertexShader);
        glDeleteShader(this.fragmentShader);
    }

    public void setVector3f(String name, Vector3f vector) {
        int location = glGetUniformLocation(this.program, name);

        glUniform3f(location, vector.x, vector.y, vector.z);
    }

    public void setMatrix4f(String name, Matrix4f matrix) {
        int location = glGetUniformLocation(this.program, name);

        FloatBuffer buffer = BufferUtils.createFloatBuffer(16);

        matrix.get(buffer);

        glUniformMatrix4fv(location, false, buffer);
    }

    public void use() {
        glUseProgram(this.program);
    }

    public void detach() {
        glUseProgram(0);
    }
}
