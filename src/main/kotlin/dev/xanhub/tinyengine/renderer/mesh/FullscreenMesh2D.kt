package dev.xanhub.tinyengine.renderer.mesh

import dev.xanhub.tinyengine.core.scene.Scene
import dev.xanhub.tinyengine.renderer.Window
import dev.xanhub.tinyengine.renderer.framebuffer.FrameBuffer
import dev.xanhub.tinyengine.renderer.shader.PostProcessor
import org.joml.Vector2f
import org.joml.Vector3i
import org.lwjgl.opengl.ARBVertexArrayObject.glBindVertexArray
import org.lwjgl.opengl.GL11.glDrawElements
import org.lwjgl.opengl.GL30

val vb = VertexBuffer2D(4).apply {
    put(Vector2f(-1f, -1f), Vector2f(0f, 0f))
    put(Vector2f(1f, -1f), Vector2f(1f, 0f))
    put(Vector2f(-1f, 1f), Vector2f(0f, 1f))
    put(Vector2f(1f, 1f), Vector2f(1f, 1f))
}

object FullscreenMesh2D: Mesh<VertexBuffer2D>(vb, listOf(Vector3i(0, 1, 3), Vector3i(0, 2, 3))) {
    fun draw(window: Window, scene: Scene, shader: PostProcessor, framebuffer: FrameBuffer) {
        shader.use()
        shader.updateUniforms(window, scene, framebuffer)
        glBindVertexArray(vertexArrayObject)
        glDrawElements(GL30.GL_TRIANGLES, faces.size * 3, GL30.GL_UNSIGNED_INT, 0)
    }
}