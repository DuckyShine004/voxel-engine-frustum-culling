package com.duckyshine.app.buffer;

import static org.lwjgl.opengl.GL30.*;

public class AABBBuffer extends Buffer {
    public AABBBuffer() {
        super();

        this.initialise();
    }

    private void initialise() {
        super.setupBuffers();

        this.bindVertexArray();

        glBindBuffer(GL_ARRAY_BUFFER, this.vertexBufferId);
        glBufferData(GL_ARRAY_BUFFER, 0, GL_DYNAMIC_DRAW);
        this.setVertexAttributePointer(0, 3, GL_FLOAT, 3 * Float.BYTES, 0);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.indexBufferId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, 0, GL_DYNAMIC_DRAW);

        this.detachVertexArray();
    }

    public void setup(BufferData bufferData) {
        this.bindVertexArray();

        glBindBuffer(GL_ARRAY_BUFFER, this.vertexBufferId);
        glBufferData(GL_ARRAY_BUFFER, bufferData.getVertices(), GL_DYNAMIC_DRAW);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.indexBufferId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, bufferData.getIndices(), GL_DYNAMIC_DRAW);
    }
}
