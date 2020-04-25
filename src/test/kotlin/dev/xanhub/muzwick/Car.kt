package dev.xanhub.muzwick

import dev.xanhub.tinyengine.core.gameobject.GameObject
import dev.xanhub.tinyengine.core.scene.Scene
import dev.xanhub.tinyengine.renderer.material.FileTexture
import dev.xanhub.tinyengine.renderer.material.Material
import dev.xanhub.tinyengine.renderer.mesh.MeshLoader
import dev.xanhub.tinyengine.renderer.shader.MinimalShader
import org.joml.*
import org.lwjgl.glfw.GLFW.*
import java.io.File
import kotlin.math.sign

class Car(scene: Scene) {
    val gameObj = GameObject(
        scene,
        MeshLoader.loadSingleMinimal(File("meshes/car.obj")),
        Material(
            FileTexture.get("textures/car.png"),
            Vector3f(.5f), 4f, false
        ),
        MinimalShader
    )
    var acceleration: Vector2f = Vector2f()
    var velocity = Vector2f()
    var carAngle = 0f
    var wheelAngle = 0f

    init {
        gameObj.transform.origin = Vector3f(3.75f, 0f, 0f)
        gameObj.transform.localScale = Vector3f(0.5f)
        Game.clock.tickListeners.add(0) { onTick() }
    }

    private fun onTick() {
        updateAcceleration()
        updateWheelAngle()
        updateVelocity()
        updateCarAngle()
        updateTransforms()
    }

    private fun updateAcceleration() {
        when(GLFW_PRESS) {
            glfwGetKey(Game.window.id, GLFW_KEY_W) -> {
                acceleration = localAxis().getColumn(0, Vector2f()).mul(25f)
            }
            glfwGetKey(Game.window.id, GLFW_KEY_S) -> {
                acceleration = localAxis().getColumn(0, Vector2f()).negate().mul(25f)
            }
            else -> {
                acceleration = Vector2f()
            }
        }
    }

    private fun updateWheelAngle() {
        when(GLFW_PRESS) {
            glfwGetKey(Game.window.id, GLFW_KEY_A) ->
                if(wheelAngle < Math.toRadians(45f)) wheelAngle += Game.clock.deltaTimef * 2f
            glfwGetKey(Game.window.id, GLFW_KEY_D) ->
                if(wheelAngle > Math.toRadians(-45f)) wheelAngle -= Game.clock.deltaTimef * 2f
            else ->
                wheelAngle -= Game.clock.deltaTimef * sign(wheelAngle)
        }
    }

    private fun updateVelocity() {
        // apply friction
        velocity = localAxis().transform(localVelocity().mul(
            1 - (.75f * Game.clock.deltaTimef / (1 + acceleration.length() / 25)),
            1 - (.8f * Game.clock.deltaTimef)
        ))

        // apply acceleration
        velocity.add(acceleration.mul(Game.clock.deltaTimef, Vector2f()))
    }

    private fun updateCarAngle() {
        val steerAngle: Float = carAngle + wheelAngle
        carAngle += (steerAngle - carAngle) * Game.clock.deltaTimef * 4f * (1 - 1/(velocity.length()/30 + 1))
    }

    private fun updateTransforms() {
        gameObj.transform.localTranslation = gameObj.transform.localTranslation.add(
            velocity.x * Game.clock.deltaTimef,
            0f,
            velocity.y * Game.clock.deltaTimef,
            Vector3f()
        )

        this.gameObj.transform.localRotation = Quaternionf().rotateY(carAngle)

        if(Game.scene != null) {
            val c = Game.scene!!.camera
            c.position = gameObj.transform.localTranslation.add(0f, 50f, 0f, Vector3f())
        }
    }

    fun forwardVec() = Vector2f(Math.cos(-carAngle), Math.sin(-carAngle))

    fun rightVec() = Matrix2f().rotate(Math.toRadians(90f)).transform(forwardVec())

    fun localAxis() = Matrix2f(forwardVec(), rightVec())

    fun localAxisInv() = localAxis().transpose()

    fun localVelocity() = localAxisInv().transform(velocity)
}