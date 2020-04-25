package dev.xanhub.tinyengine.renderer.mesh

import org.lwjgl.opengl.GL11.GL_FLOAT
import org.lwjgl.opengl.GL11.GL_INT

enum class VertexAttribute(val size: Int, val internalType: Int, val totalBytes: Int, val std140Size: Int) {
    FLOAT(1, GL_FLOAT, 4, 1),
    INT(1, GL_INT, 4, 1),
    VEC2F(2, GL_FLOAT, 8, 2),
    VEC3F(3, GL_FLOAT, 12, 4),
    VEC4F(4, GL_FLOAT, 16, 4),
    MAT4(16, GL_FLOAT, 64, 16),
    MAT3(9, GL_FLOAT, 36, 12)
}