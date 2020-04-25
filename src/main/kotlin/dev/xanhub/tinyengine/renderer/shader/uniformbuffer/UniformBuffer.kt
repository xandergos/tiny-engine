package dev.xanhub.tinyengine.renderer.shader.uniformbuffer

import dev.xanhub.tinyengine.renderer.shader.Shader
import org.joml.*
import org.lwjgl.opengl.ARBUniformBufferObject.*
import org.lwjgl.opengl.GL15C.*
import org.lwjgl.system.MemoryStack.stackPush

/**
 * Represents a uniform buffer, which can be used to quickly map a group of
 * uniforms to a uniform block.
 *
 * Using any set() function with a uniform not present in this buffer
 * has undefined behaviour.
 *
 * @param shader The shader this buffer will be used on.
 * @param blockName The name of the uniform block this buffer will be bound with.
 */
abstract class UniformBuffer(shader: Shader, blockName: String) {
    private val id: Int = glGenBuffers()
    private val blockIndex = glGetUniformBlockIndex(shader.id, blockName)

    init {
        val size = glGetActiveUniformBlocki(shader.id, blockIndex, GL_UNIFORM_BLOCK_DATA_SIZE)
        glBindBuffer(GL_UNIFORM_BUFFER, id)
        glBufferData(GL_UNIFORM_BUFFER, size.toLong(), GL_DYNAMIC_DRAW)
        glBindBuffer(GL_UNIFORM_BUFFER, 0)
    }

    open fun bind(bindingPoint: Int) {
        glBindBufferBase(GL_UNIFORM_BUFFER, bindingPoint, id)
    }
    
    protected fun set(uniform: Uniform, v: Float) {
        glBindBuffer(GL_UNIFORM_BUFFER, id)
        val data = intArrayOf(v.toBits())
        glBufferSubData(GL_UNIFORM_BUFFER, uniform.offset.toLong(), data)
        glBindBuffer(GL_UNIFORM_BUFFER, 0)
    }

    protected fun set(uniform: Uniform, v: Int) {
        glBindBuffer(GL_UNIFORM_BUFFER, id)
        val data = intArrayOf(v)
        glBufferSubData(GL_UNIFORM_BUFFER, uniform.offset.toLong(), data)
        glBindBuffer(GL_UNIFORM_BUFFER, 0)
    }

    protected fun set(uniform: Uniform, v: Vector2fc) {
        glBindBuffer(GL_UNIFORM_BUFFER, id)
        stackPush().use { stack ->
            val data = stack.mallocInt(2)
            data.put(v.x().toBits())
            data.put(v.y().toBits())
            data.flip()
            glBufferSubData(GL_UNIFORM_BUFFER, uniform.offset.toLong(), data)
        }
        glBindBuffer(GL_UNIFORM_BUFFER, 0)
    }

    protected fun set(uniform: Uniform, v: Vector3fc) {
        glBindBuffer(GL_UNIFORM_BUFFER, id)
        stackPush().use { stack ->
            val data = stack.mallocInt(3)
            data.put(v.x().toBits())
            data.put(v.y().toBits())
            data.put(v.z().toBits())
            data.flip()
            glBufferSubData(GL_UNIFORM_BUFFER, uniform.offset.toLong(), data)
        }
        glBindBuffer(GL_UNIFORM_BUFFER, 0)
    }

    protected fun set(uniform: Uniform, v: Vector4fc) {
        glBindBuffer(GL_UNIFORM_BUFFER, id)
        stackPush().use { stack ->
            val data = stack.mallocInt(4)
            data.put(v.x().toBits())
            data.put(v.y().toBits())
            data.put(v.z().toBits())
            data.put(v.w().toBits())
            data.flip()
            glBufferSubData(GL_UNIFORM_BUFFER, uniform.offset.toLong(), data)
        }
        glBindBuffer(GL_UNIFORM_BUFFER, 0)
    }

    protected fun set(uniform: Uniform, v: Matrix3fc) {
        glBindBuffer(GL_UNIFORM_BUFFER, id)
        stackPush().use { stack ->
            val data = stack.mallocInt(9)
            for (i in 0 until 9)
                data.put(v[i / 3, i % 3].toBits())
            data.flip()
            glBufferSubData(GL_UNIFORM_BUFFER, uniform.offset.toLong(), data)
        }
        glBindBuffer(GL_UNIFORM_BUFFER, 0)
    }

    protected fun set(uniform: Uniform, v: Matrix4fc) {
        glBindBuffer(GL_UNIFORM_BUFFER, id)
        stackPush().use { stack ->
            val data = stack.mallocInt(16)
            for (i in 0 until 16)
                data.put(v[i / 4, i % 4].toBits())
            data.flip()
            glBufferSubData(GL_UNIFORM_BUFFER, uniform.offset.toLong(), data)
        }
        glBindBuffer(GL_UNIFORM_BUFFER, 0)
    }
}