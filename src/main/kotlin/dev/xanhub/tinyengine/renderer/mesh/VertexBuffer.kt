package dev.xanhub.tinyengine.renderer.mesh

import org.joml.*
import org.lwjgl.BufferUtils.createIntBuffer
import org.lwjgl.opengl.GL15.*
import org.lwjgl.opengl.GL20.glEnableVertexAttribArray
import org.lwjgl.opengl.GL20.glVertexAttribPointer
import java.nio.IntBuffer

abstract class VertexBuffer(val numVertices: Int, private val vertexLayout: Array<VertexAttribute>) {
    var locked: Boolean = false; private set
    var vboID: Int? = null; private set
    private val attribOffsets = List(vertexLayout.size) { i ->
        var s = 0L
        for(j in 0 until i)
            s += vertexLayout[j].size
        s
    }
    private val stride = vertexLayout.sumBy { a -> a.size }
    private val rawBuffer: IntBuffer = createIntBuffer(numVertices * stride)

    /**
     * @param bufferUse One of GL_STATIC_DRAW, GL_DYNAMIC_DRAW, or GL_STREAM_DRAW
     */
    fun bindToVBO(bufferUse: Int = GL_STATIC_DRAW) {
        locked = true
        rawBuffer.flip()
        vboID = glGenBuffers()
        glBindBuffer(GL_ARRAY_BUFFER, vboID!!)
        glBufferData(GL_ARRAY_BUFFER, rawBuffer, bufferUse)


        for(i in vertexLayout.indices) {
            val size = vertexLayout[i].size
            val dataType = vertexLayout[i].internalType
            glVertexAttribPointer(i, size, dataType, false, stride * 4, attribOffsets[i] * 4)
            glEnableVertexAttribArray(i)
        }
    }

    abstract fun getPosition(vertexIndex: Int): Vector3f

    protected fun getFloat(vertexIndex: Int, attributeIndex: Int): Float {
        assert(vertexLayout[attributeIndex] == VertexAttribute.FLOAT)
        val ptr = vertexIndex * stride + attribOffsets[attributeIndex].toInt()
        return Float.fromBits(this.rawBuffer.get(ptr))
    }

    protected fun getInt(vertexIndex: Int, attributeIndex: Int): Int {
        assert(vertexLayout[attributeIndex] == VertexAttribute.INT)
        val ptr = vertexIndex * stride + attribOffsets[attributeIndex].toInt()
        return this.rawBuffer.get(ptr)
    }

    protected fun getVector2f(vertexIndex: Int, attributeIndex: Int): Vector2f {
        assert(vertexLayout[attributeIndex] == VertexAttribute.VEC2F)
        val ptr = vertexIndex * stride + attribOffsets[attributeIndex].toInt()
        return Vector2f(
            Float.fromBits(this.rawBuffer.get(ptr)),
            Float.fromBits(this.rawBuffer.get(ptr + 1))
        )
    }

    protected fun getVector3f(vertexIndex: Int, attributeIndex: Int): Vector3f {
        assert(vertexLayout[attributeIndex] == VertexAttribute.VEC2F)
        val ptr = vertexIndex * stride + attribOffsets[attributeIndex].toInt()
        return Vector3f(
            Float.fromBits(this.rawBuffer.get(ptr)),
            Float.fromBits(this.rawBuffer.get(ptr + 1)),
            Float.fromBits(this.rawBuffer.get(ptr + 2))
        )
    }

    protected fun getVector4f(vertexIndex: Int, attributeIndex: Int): Vector4f {
        assert(vertexLayout[attributeIndex] == VertexAttribute.VEC2F)
        val ptr = vertexIndex * stride + attribOffsets[attributeIndex].toInt()
        return Vector4f(
            Float.fromBits(this.rawBuffer.get(ptr)),
            Float.fromBits(this.rawBuffer.get(ptr + 1)),
            Float.fromBits(this.rawBuffer.get(ptr + 2)),
            Float.fromBits(this.rawBuffer.get(ptr + 3))
        )
    }

    protected fun put(v: Float) {
        if(locked) throw IllegalStateException("Buffer locked.")
        rawBuffer.put(v.toBits())
    }

    protected fun put(v: Int) {
        if(locked) throw IllegalStateException("Buffer locked.")
        rawBuffer.put(v)
    }

    protected fun put(v: Vector2fc) {
        put(v.x())
        put(v.y())
    }

    protected fun put(v: Vector3fc) {
        put(v.x())
        put(v.y())
        put(v.z())
    }

    protected fun put(v: Vector4fc) {
        put(v.x())
        put(v.y())
        put(v.z())
        put(v.w())
    }

    protected fun put(v: Matrix4fc) {
        for(i in 0 until 4) {
            for(j in 0 until 4) {
                put(v[i, j].toBits())
            }
        }
    }

    protected fun put(v: Matrix3fc) {
        for(i in 0 until 3) {
            for(j in 0 until 3) {
                put(v[i, j].toBits())
            }
        }
    }

    fun destroy() {
        glDeleteBuffers(vboID!!)
    }
}
