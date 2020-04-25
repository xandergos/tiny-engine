package dev.xanhub.tinyengine.renderer.shader.uniformbuffer

import dev.xanhub.tinyengine.renderer.shader.Shader
import org.lwjgl.opengl.GL31.*
import org.lwjgl.system.MemoryStack.stackPush

class Uniform(val shader: Shader, val name: String) {
    companion object {
        /**
         * Creates a list of uniforms. This method is significantly faster
         * than adding each uniform to a list individually, since
         * their properties can all be fetched at once.
         */
        fun listOf(shader: Shader, vararg names: String): List<Uniform> {
            stackPush().use { stack ->
                val indices = stack.mallocInt(names.size)
                val offsets = stack.mallocInt(names.size)

                glGetUniformIndices(shader.id, names, indices)
                glGetActiveUniformsiv(shader.id, indices, GL_UNIFORM_OFFSET, offsets)

                return List(names.size) { i -> Uniform(shader, names[i], indices[i], offsets[i]) }
            }
        }
    }

    private constructor(shader: Shader, name: String, index: Int, offset: Int): this(shader, name) {
        this.index = index
        this.offset = offset
    }

    var index = -1; private set
    var offset = -1; private set

    init {
        if(index == -1) index = glGetUniformIndices(shader.id, name)
        if(offset == -1) {
            stackPush().use { stack ->
                val offset = stack.ints(0)
                val index = stack.ints(index)
                glGetActiveUniformsiv(shader.id, index, GL_UNIFORM_OFFSET, offset)
                this.offset = offset.get()
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Uniform

        if (index != other.index) return false

        return true
    }

    override fun hashCode(): Int = index
}