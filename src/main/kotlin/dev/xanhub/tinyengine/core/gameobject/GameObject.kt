package dev.xanhub.tinyengine.core.gameobject

import dev.xanhub.tinyengine.core.scene.Scene
import dev.xanhub.tinyengine.renderer.Drawable
import dev.xanhub.tinyengine.renderer.Window
import dev.xanhub.tinyengine.renderer.material.Material
import dev.xanhub.tinyengine.renderer.math.TransformBuffered
import dev.xanhub.tinyengine.renderer.math.Transformable
import dev.xanhub.tinyengine.renderer.mesh.Mesh
import dev.xanhub.tinyengine.renderer.mesh.VertexBuffer
import dev.xanhub.tinyengine.renderer.shader.ObjectShader
import org.joml.Math
import org.joml.Vector3f
import org.joml.Vector4f
import org.lwjgl.glfw.GLFW.glfwGetTime
import kotlin.random.Random

private val r = Random(5829578186)
private const val updateCullDelay = 5
var renderDistance: Int = 1100

open class GameObject(
    owner: Scene,
    var mesh: Mesh<out VertexBuffer>,
    var material: Material,
    shader: ObjectShader,
    var preDraw: Runnable? = null,
    var postDraw: Runnable? = null
): Transformable, Drawable {
    private var lastCulledUpdate = glfwGetTime().toFloat() - r.nextFloat() * updateCullDelay
    private var culled: Boolean? = null
    override var transform: TransformBuffered = TransformBuffered(shader); protected set
    var hidden: Boolean = false
    var owner: Scene = owner; private set
    var collisionObject: GameObjectCollider? = null
        set(value) {
            field?.owner = null
            field = value
            field?.owner = this
        }
    var shader = shader
        set(value) {
            this.transform = TransformBuffered(
                transform.shader,
                transform.localTranslation,
                transform.localRotation,
                transform.localScale
            ).apply { origin = transform.origin }
            field = value
        }

    init {
        (owner.gameObjects as ArrayList).add(this)
    }

    override fun draw(window: Window) {
        preDraw?.run()
        if(!hidden && !isCulled()) {
            mesh.drawFlatShader(owner, shader, this)
        }
        postDraw?.run()
    }

    fun changeScene(scene: Scene) {
        (owner.gameObjects as ArrayList).remove(this)
        this.owner = scene
        this.culled = null
        (scene.gameObjects as ArrayList).add(this)
    }

    private fun isCulled(): Boolean {
        val timeSinceUpdate = glfwGetTime().toFloat() - lastCulledUpdate
        if(culled == null || timeSinceUpdate > updateCullDelay) {
            lastCulledUpdate += updateCullDelay * Math.floor(timeSinceUpdate / updateCullDelay)
            val center4 = this.transform.globalTransformMatrix.transform(Vector4f(mesh.center, 1f))
            val center3 = Vector3f(center4.x, center4.y, center4.z)
            culled = center3.distanceSquared(owner.camera.position) > renderDistance * renderDistance
        }

        return culled!!
    }
}