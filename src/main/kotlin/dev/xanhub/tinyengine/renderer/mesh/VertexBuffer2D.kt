package dev.xanhub.tinyengine.renderer.mesh

import org.joml.Vector2f
import org.joml.Vector3f

class VertexBuffer2D(numVertices: Int) : VertexBuffer(numVertices, arrayOf(VertexAttribute.VEC2F, VertexAttribute.VEC2F)) {
    fun put(pos: Vector2f, texCoords: Vector2f) {
        this.put(pos)
        this.put(texCoords)
    }

    override fun getPosition(vertexIndex: Int): Vector3f {
        val p = getVector2f(vertexIndex, 0)
        return Vector3f(p.x, p.y, 0f)
    }
}