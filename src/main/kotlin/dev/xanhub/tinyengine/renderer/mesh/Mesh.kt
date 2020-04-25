package dev.xanhub.tinyengine.renderer.mesh

import dev.xanhub.tinyengine.core.gameobject.GameObject
import dev.xanhub.tinyengine.core.scene.Scene
import dev.xanhub.tinyengine.renderer.material.Material
import dev.xanhub.tinyengine.renderer.math.TransformBuffered
import dev.xanhub.tinyengine.renderer.shader.FlatShader
import dev.xanhub.tinyengine.renderer.shader.MeshShader
import dev.xanhub.tinyengine.renderer.shader.ObjectShader
import dev.xanhub.tinyengine.renderer.shader.Shader
import org.joml.Vector3f
import org.joml.Vector3i
import org.lwjgl.opengl.GL30.*
import java.awt.Color

open class Mesh<V: VertexBuffer>(val vertexBuffer: V, val faces: List<Vector3i>) {
    protected val vertexArrayObject: Int = glGenVertexArrays()
    private val elementBufferObject: Int = glGenBuffers()
    val center: Vector3f by lazy {
        val posSum = Vector3f()
        for(i in 0 until vertexBuffer.numVertices) {
            posSum.add(vertexBuffer.getPosition(i))
        }
        posSum.div(vertexBuffer.numVertices.toFloat())
    }


    init {
        glBindVertexArray(vertexArrayObject)

        vertexBuffer.bindToVBO()

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, elementBufferObject)
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, IntArray(faces.size * 3) { i ->
            faces[i/3][i%3]
        }, GL_STATIC_DRAW)

        glBindBuffer(GL_ARRAY_BUFFER, 0)
        glBindVertexArray(0)
    }

    fun drawFlatShader(
        scene: Scene,
        objectShader: ObjectShader,
        parentObject: GameObject
    ) {
        objectShader.use()
        objectShader.updateUniforms(scene, parentObject)
        glBindVertexArray(vertexArrayObject)
        glDrawElements(GL_TRIANGLES, faces.size * 3, GL_UNSIGNED_INT, 0)
    }

    fun drawFlatShader(
        scene: Scene,
        meshShader: MeshShader,
        material: Material,
        transform: TransformBuffered
    ) {
        meshShader.use()
        meshShader.updateUniforms(scene, this, material, transform)
        glBindVertexArray(vertexArrayObject)
        glDrawElements(GL_TRIANGLES, faces.size * 3, GL_UNSIGNED_INT, 0)
    }

    fun drawFlatShader(
        scene: Scene,
        color: Color,
        transform: TransformBuffered
    ) {
        FlatShader.use()
        FlatShader.updateUniforms(scene, color, transform)
        glBindVertexArray(vertexArrayObject)
        glDrawElements(GL_TRIANGLES, faces.size * 3, GL_UNSIGNED_INT, 0)
    }

    /**
     * Draws the mesh without settings shader uniforms first. Make sure to do so yourself
     * if necessary before calling this method.
     */
    fun drawBlind(shader: Shader) {
        shader.use()
        glBindVertexArray(vertexArrayObject)
        glDrawElements(GL_TRIANGLES, faces.size * 3, GL_UNSIGNED_INT, 0)
    }

    fun destroy() {
        glDeleteVertexArrays(vertexArrayObject)
        vertexBuffer.destroy()
        glDeleteBuffers(elementBufferObject)
    }
}