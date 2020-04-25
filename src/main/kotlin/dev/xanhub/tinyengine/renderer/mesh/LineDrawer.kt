package dev.xanhub.tinyengine.renderer.mesh

import dev.xanhub.tinyengine.core.scene.Scene
import dev.xanhub.tinyengine.renderer.shader.FlatShader
import org.joml.Matrix4f
import org.joml.Vector3f
import org.joml.Vector3fc
import org.joml.Vector4f
import org.lwjgl.opengl.GL30.*

private val lineVB = object: VertexBuffer(2, arrayOf(VertexAttribute.VEC3F)) {
    init {
        this.put(Vector3f())
        this.put(Vector3f(0f, 0f, 1f))
    }

    override fun getPosition(vertexIndex: Int): Vector3f {
        return this.getVector3f(0, 0)
    }
}
private val lineArrayObj = glGenVertexArrays().apply {
    glBindVertexArray(this)
    lineVB.bindToVBO()
    glBindVertexArray(0)
}

object LineDrawer {
    private val lineMatrix = Matrix4f()
    fun draw(scene: Scene, p1: Vector3fc, p2: Vector3fc, color: Vector3fc) {
        lineMatrix.setColumn(2, Vector4f(p2.sub(p1, Vector3f()), 0f))
        lineMatrix.setColumn(3, Vector4f(p1, 1f))
        glBindVertexArray(lineArrayObj)
        FlatShader.updateUniforms(Vector3f(color.x(), color.y(), color.z()), scene.camera.transformMatrix, lineMatrix)
        FlatShader.use()
        glDrawArrays(GL_LINES, 0, 2)
    }

    fun drawBoxOutline(scene: Scene, p1: Vector3fc, p2: Vector3fc, color: Vector3fc) {
        draw(scene, p1, Vector3f(p1.x(), p1.y(), p2.z()), color) // idk why i need to draw the first one twice
        draw(scene, p1, Vector3f(p1.x(), p1.y(), p2.z()), color)
        draw(scene, p1, Vector3f(p1.x(), p2.y(), p1.z()), color)
        draw(scene, p1, Vector3f(p2.x(), p1.y(), p1.z()), color)

        draw(scene, p2, Vector3f(p2.x(), p2.y(), p1.z()), color)
        draw(scene, p2, Vector3f(p2.x(), p1.y(), p2.z()), color)
        draw(scene, p2, Vector3f(p1.x(), p2.y(), p2.z()), color)

        val p11 = Vector3f(p1.x(), p2.y(), p1.z())
        val p21 = Vector3f(p2.x(), p1.y(), p2.z())

        draw(scene, p11, Vector3f(p11.x, p11.y, p21.z), color)
        draw(scene, p11, Vector3f(p21.x, p11.y, p11.z), color)

        draw(scene, p21, Vector3f(p21.x, p21.y, p11.z), color)
        draw(scene, p21, Vector3f(p11.x, p21.y, p21.z), color)

        draw(scene, Vector3f(p1.x(), p1.y(), p2.z()), Vector3f(p1.x(), p2.y(), p2.z()), color)
        draw(scene, Vector3f(p2.x(), p1.y(), p1.z()), Vector3f(p2.x(), p2.y(), p1.z()), color)
    }
}