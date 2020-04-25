package dev.xanhub.tinyengine.core.gameobject.sprite

import dev.xanhub.tinyengine.core.gameobject.GameObject
import dev.xanhub.tinyengine.core.scene.Scene
import dev.xanhub.tinyengine.renderer.material.Material
import dev.xanhub.tinyengine.renderer.mesh.DetailedVertexBuffer
import dev.xanhub.tinyengine.renderer.mesh.Mesh
import dev.xanhub.tinyengine.renderer.shader.ObjectShader
import org.joml.*

abstract class Sprite(
    owner: Scene,
    material: Material,
    shader: ObjectShader,
    preDraw: Runnable? = null,
    postDraw: Runnable? = null
) : GameObject(owner, mesh, material, shader, preDraw, postDraw) {
    companion object {
        private val mesh = Mesh(
            DetailedVertexBuffer(4).apply {
                this.put(
                    Vector3f(-.5f, -.5f, 0f),
                    Vector3f(0f, 0f, -1f),
                    Vector2f(0f, 0f),
                    Vector3f(1f, 0f, 0f),
                    Vector3f(0f, -1f, 0f)
                )
                this.put(
                    Vector3f(.5f, -.5f, 0f),
                    Vector3f(0f, 0f, -1f),
                    Vector2f(1f, 0f),
                    Vector3f(1f, 0f, 0f),
                    Vector3f(0f, -1f, 0f)
                )
                this.put(
                    Vector3f(-.5f, .5f, 0f),
                    Vector3f(0f, 0f, -1f),
                    Vector2f(0f, 1f),
                    Vector3f(1f, 0f, 0f),
                    Vector3f(0f, -1f, 0f)
                )
                this.put(
                    Vector3f(.5f, .5f, 0f),
                    Vector3f(0f, 0f, -1f),
                    Vector2f(1f, 1f),
                    Vector3f(1f, 0f, 0f),
                    Vector3f(0f, -1f, 0f)
                )
            },
            listOf(Vector3i(0, 1, 3), Vector3i(0, 2, 3))
        )
    }

    fun lookAt(pos: Vector3fc) {
        val along = pos.sub(
            transform.localTranslation,
            Vector3f()
        ).negate()
        along.y = 0f
        this.transform.localRotation = Vector3f(0f, 0f, 1f).rotationTo(along.normalize(), Quaternionf())
    }
}