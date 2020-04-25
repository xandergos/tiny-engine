package dev.xanhub.tinyengine.renderer.shader

import dev.xanhub.tinyengine.renderer.shader.uniformbuffer.UniformBuffer
import org.joml.*
import org.lwjgl.opengl.GL31.*
import org.lwjgl.system.MemoryStack.stackPush
import java.io.File

private var currentProgram = -1

class VertexShaderCompileError(msg: String): Exception(msg)
class FragmentShaderCompileError(msg: String): Exception(msg)
class ProgramLinkError(msg: String): Exception(msg)

abstract class Shader(
    vertexFile: File,
    fragmentFile: File
) {
    constructor(vertexPath: String, fragmentPath: String): this(File(vertexPath), File(fragmentPath))

    private val uniformLocations: HashMap<String, Int> = HashMap()
    private val uniformBlockLocations: HashMap<String, Int> = HashMap()
    val id: Int

    init {
        val vertexShader = glCreateShader(GL_VERTEX_SHADER)
        glShaderSource(vertexShader, vertexFile.readText())
        glCompileShader(vertexShader)
        stackPush().use { stack ->
            val status = stack.mallocInt(1)
            glGetShaderiv(vertexShader, GL_COMPILE_STATUS, status)

            if(status.get() == GL_FALSE)
                throw VertexShaderCompileError(glGetShaderInfoLog(vertexShader))
        }


        val fragmentShader = glCreateShader(GL_FRAGMENT_SHADER)
        glShaderSource(fragmentShader, fragmentFile.readText())
        glCompileShader(fragmentShader)
        stackPush().use { stack ->
            val status = stack.mallocInt(1)
            glGetShaderiv(fragmentShader, GL_COMPILE_STATUS, status)

            if(status.get() == GL_FALSE)
                throw FragmentShaderCompileError(glGetShaderInfoLog(fragmentShader))
        }

        id = glCreateProgram()
        glAttachShader(id, vertexShader)
        glAttachShader(id, fragmentShader)
        glLinkProgram(id)

        stackPush().use { stack ->
            val status = stack.mallocInt(1)
            glGetProgramiv(id, GL_LINK_STATUS, status)

            if(status.get() == GL_FALSE) {
                throw ProgramLinkError(glGetProgramInfoLog(id))
            }
        }

        // Shaders can be deleted once linked to program
        glDeleteShader(vertexShader)
        glDeleteShader(fragmentShader)
    }

    fun use() {
        if(currentProgram != id) {
            currentProgram = id
            glUseProgram(id)
        }
    }

    fun setUniformBool(name: String, value: Boolean) {
        glUniform1i(getUniformPos(name), if(value) 1 else 0)
    }

    fun setUniformInt(name: String, value: Int) {
        glUniform1i(getUniformPos(name), value)
    }

    fun setUniformFloat(name: String, value: Float) {
        glUniform1f(getUniformPos(name), value)
    }

    fun setUniformVec3f(name: String, value: Vector3fc) {
        glUniform3f(getUniformPos(name), value.x(), value.y(), value.z())
    }

    fun setUniformVec2f(name: String, value: Vector2fc) {
        glUniform2f(getUniformPos(name), value.x(), value.y())
    }

    fun setUniformVec3i(name: String, value: Vector3ic) {
        glUniform3i(getUniformPos(name), value.x(), value.y(), value.z())
    }

    fun setUniformVec2i(name: String, value: Vector2ic) {
        glUniform2i(getUniformPos(name), value.x(), value.y())
    }

    fun setUniformMat3f(name: String, value: Matrix3fc) {
        stackPush().use { stack ->
            val loc = getUniformPos(name)
            glUniformMatrix3fv(loc, false, value.get(stack.mallocFloat(9)))
        }
    }

    fun setUniformMat4f(name: String, value: Matrix4fc) {
        stackPush().use { stack ->
            val loc = getUniformPos(name)
            glUniformMatrix4fv(loc, false, value.get(stack.mallocFloat(16)))
        }
    }

    fun setUniformBlock(blockName: String, uniformBuffer: UniformBuffer, bindingPoint: Int) {
        val blockPos = getUniformBlockIndex(blockName)
        glUniformBlockBinding(id, blockPos, bindingPoint)
        uniformBuffer.bind(bindingPoint)
    }

    private fun getUniformPos(name: String): Int {
        return uniformLocations.getOrPut(name, {
            glGetUniformLocation(id, name)
        })
    }

    private fun getUniformBlockIndex(name: String): Int {
        return uniformBlockLocations.getOrPut(name, {
            glGetUniformBlockIndex(id, name)
        })
    }

    override fun equals(other: Any?): Boolean = this === other

    override fun hashCode(): Int = id
}