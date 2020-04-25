package dev.xanhub.muzwick

import com.bulletphysics.collision.broadphase.DbvtBroadphase
import com.bulletphysics.collision.dispatch.CollisionDispatcher
import com.bulletphysics.collision.dispatch.CollisionObject
import com.bulletphysics.collision.dispatch.CollisionWorld
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration
import com.bulletphysics.collision.shapes.BoxShape
import com.bulletphysics.dynamics.DiscreteDynamicsWorld
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver
import com.bulletphysics.linearmath.DebugDrawModes
import com.bulletphysics.linearmath.Transform
import dev.xanhub.muzwick.generator.SkyscraperGen
import dev.xanhub.muzwick.generator.shader
import dev.xanhub.tinyengine.core.gameobject.GameObject
import dev.xanhub.tinyengine.core.gameobject.GameObjectCollider
import dev.xanhub.tinyengine.core.gameobject.sprite.StaticSprite
import dev.xanhub.tinyengine.core.scene.Scene
import dev.xanhub.tinyengine.renderer.Window
import dev.xanhub.tinyengine.renderer.bulletdebug.BltDebugDrawer
import dev.xanhub.tinyengine.renderer.camera.YawPitchCamera
import dev.xanhub.tinyengine.renderer.light.Spotlight
import dev.xanhub.tinyengine.renderer.material.FileTexture
import dev.xanhub.tinyengine.renderer.material.Material
import dev.xanhub.tinyengine.renderer.mesh.LineDrawer
import dev.xanhub.tinyengine.util.Resource
import dev.xanhub.tinyengine.util.toJavax
import org.joml.*
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.glLineWidth
import kotlin.math.PI
import kotlin.math.max
import kotlin.math.min

object BuildingNavigationScene: Scene(YawPitchCamera(Vector3f(0f, 0.29f, 0f), 90f, 0f, Game.window.size, 90f)) {
    val road = kotlin.run {
        val sprite = StaticSprite(
            this,
            Material(
                FileTexture.get(
                    Resource.get("textures/gray.png").absolutePath,
                    minFilter = GL11.GL_NEAREST,
                    useMipmaps = false
                ), Vector3f(0.05f), 32f, false
            ),
            shader
        )
        sprite.transform.localTranslation = Vector3f(0f, 0f, 0f)
        sprite.transform.localScale = Vector3f(960f, 960f, 1f)
        sprite.transform.localRotation = sprite.transform.localRotation.rotateX(Math.toRadians(90f), Quaternionf())
        sprite
    }
    val cameraLight = Spotlight(
        camera.position,
        camera.direction,
        Math.toRadians(180f),
        Math.toRadians(180f),
        Vector3f(),
        Vector3f(1f),
        Vector3f(1f)
    )
    private var mouseLocked = false
        set(value) {
            if(value == field) return

            if(value) glfwSetInputMode(Game.window.id, GLFW_CURSOR, GLFW_CURSOR_DISABLED)
            else glfwSetInputMode(Game.window.id, GLFW_CURSOR, GLFW_CURSOR_NORMAL)
            field = value
        }
    val dynamicsWorld = kotlin.run {
        val config = DefaultCollisionConfiguration()
        val dispather = CollisionDispatcher(config)
        val pairCache = DbvtBroadphase()
        val solver = SequentialImpulseConstraintSolver()
        return@run DiscreteDynamicsWorld(dispather, pairCache, solver, config)
    }
    var selectedObject: GameObject? = null

    init {
        Game.clock.tickListeners.add(0) {
            if(this == Game.scene) {
                cameraLight.position = camera.position
                cameraLight.direction = camera.direction
            }
            processInputs()
        }

        postProcessor = PixelatePostProcessor

        val skyscrapers = SkyscraperGen.generate(this, Resource.get("Skyscrapers.png"))
        for(s in skyscrapers)
            dynamicsWorld.collisionWorld.addCollisionObject(s.collisionObject)

        spotlight = cameraLight

        Game.window.cursorMoveListeners.add(0) { ev -> onCursorMove(ev.prev, ev.new) }

        dynamicsWorld.debugDrawer = BltDebugDrawer(Game.window, this, DebugDrawModes.DRAW_AABB)
    }

    override fun drawGUI(window: Window) {
        super.drawGUI(window)
        val collider = selectedObject?.collisionObject ?: return
        if(collider.collisionShape is BoxShape) {
            val shape = collider.collisionShape
            val p1 = javax.vecmath.Vector3f()
            val p2 = javax.vecmath.Vector3f()
            glLineWidth(5f)
            shape.getAabb(collider.getWorldTransform(Transform()), p1, p2)
            LineDrawer.drawBoxOutline(
                    this,
                    Vector3f(p1.x, p1.y, p1.z),
                    Vector3f(p2.x, p2.y, p2.z),
                    Vector3f(0f, 0f, 0f)
            )
        }
    }

    fun processInputs() {
        orbitMove()

        if(glfwGetMouseButton(Game.window.id, GLFW_MOUSE_BUTTON_LEFT) == GLFW_PRESS) {
            val collider = getObj(Game.window.getCursorPos())
            if((collider as? GameObjectCollider)?.owner != null) {
                this.selectedObject = collider.owner
            }
        }
    }

    fun orbitMove() {
        mouseLocked = glfwGetMouseButton(Game.window.id, GLFW_MOUSE_BUTTON_MIDDLE) == GLFW_PRESS

        if(camera.pitch > -.25f) camera.pitch = -.25f
        camera.position = camera.direction.mul(-500f, Vector3f())
    }

    private fun onCursorMove(prev: Vector2ic?, new: Vector2ic) {
        prev ?: return

        if(mouseLocked) {
            val sens = 0.1f
            val offset = new.sub(prev, Vector2i())

            camera.yaw += Math.toRadians(offset.x.toFloat()) * sens
            camera.pitch = min(
                PI.toFloat()/2 - .001f,
                max(
                    -PI.toFloat()/2 + .001f,
                    camera.pitch - Math.toRadians(offset.y.toFloat() * sens)
                )
            )
        }
    }

    private fun getObj(cursorPos: Vector2i): CollisionObject? {
        val from = camera.screenToWorld(Game.window, cursorPos, -1f)
        val end = camera.screenToWorld(Game.window, cursorPos, 1f)
        val closest = CollisionWorld.ClosestRayResultCallback(from.toJavax(), end.toJavax())
        dynamicsWorld.rayTest(from.toJavax(), end.toJavax(), closest)

        return if(closest.hasHit()) {
            val collider = closest.collisionObject
            collider
        } else {
            null
        }
    }
}