package dev.xanhub.tinyengine.renderer.mesh

import org.joml.Vector2f
import org.joml.Vector2fc
import org.joml.Vector3f
import org.joml.Vector3fc

class MinimalVertexBuffer(numVertices: Int)
    : VertexBuffer(
        numVertices,
        arrayOf(
            VertexAttribute.VEC3F,
            VertexAttribute.VEC3F,
            VertexAttribute.VEC2F
        )
    ) {
    fun put(position: Vector3fc, normal: Vector3fc, texCoords: Vector2fc) {
        this.put(position)
        this.put(normal)
        this.put(texCoords)
    }

    override fun getPosition(vertexIndex: Int): Vector3f {
        return this.getVector3f(vertexIndex, 0)
    }

    fun getNormal(vertexIndex: Int): Vector3f {
        return this.getVector3f(vertexIndex, 1)
    }

    fun getTexCoords(vertexIndex: Int): Vector2f {
        return this.getVector2f(vertexIndex, 2)
    }
}