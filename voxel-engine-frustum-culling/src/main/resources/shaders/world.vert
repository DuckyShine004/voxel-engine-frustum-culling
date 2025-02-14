#version 330 core

layout(location = 0) in vec3 blockPositions;
layout(location = 1) in vec2 textureCoordinates;
layout(location = 2) in int textureIds;

out vec2 outTextureCoordinates;

out vec4 outVertexColour;

flat out int outTextureIndex;

uniform mat4 projectionViewMatrix;

void main() {
    gl_Position = projectionViewMatrix * vec4(blockPositions, 1.0);

    outTextureIndex = textureIds;

    outTextureCoordinates = textureCoordinates;

    outVertexColour = vec4(1.0, 1.0, 1.0, 1.0);
};
