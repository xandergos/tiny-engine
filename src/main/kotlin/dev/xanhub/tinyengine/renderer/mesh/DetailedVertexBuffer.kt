package dev.xanhub.tinyengine.renderer.mesh

import org.joml.Vector2f
import org.joml.Vector2fc
import org.joml.Vector3f
import org.joml.Vector3fc

class DetailedVertexBuffer(numVertices: Int)
    : VertexBuffer(
        numVertices,
        arrayOf(
            VertexAttribute.VEC3F,
            VertexAttribute.VEC3F,
            VertexAttribute.VEC2F,
            VertexAttribute.VEC3F,
            VertexAttribute.VEC3F
        )
    ) {
    fun put(position: Vector3fc, normal: Vector3fc, texCoords: Vector2fc, tangent: Vector3fc, biTangent: Vector3fc) {
        assert(normal.x() != 0f || normal.y() != 0f || normal.z() != 0f)
        this.put(position)
        this.put(normal)
        this.put(texCoords)
        this.put(tangent)
        this.put(biTangent)
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

    fun getTangent(vertexIndex: Int): Vector3f {
        return this.getVector3f(vertexIndex, 3)
    }

    fun getBiTangent(vertexIndex: Int): Vector3f {
        return this.getVector3f(vertexIndex, 4)
    }
}