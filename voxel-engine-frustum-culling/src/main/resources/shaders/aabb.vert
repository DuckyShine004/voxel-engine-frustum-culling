#version 330 core

layout(location = 0) in vec3 aabbPosition;

uniform mat4 projectionViewMatrix;

void main() {
    gl_Position = projectionViewMatrix * vec4(aabbPosition, 1.0);
}
