package dev.xanhub.tinyengine.renderer.camera

import dev.xanhub.tinyengine.renderer.Window
import dev.xanhub.tinyengine.renderer.math.Transform
import org.joml.*
import kotlin.math.PI

private val upVector = Vector3f(0f, 1f, 0f)

open class Camera(position: Vector3fc, direction: Vector3fc, var screenSize: Vector2i, fov: Float) {
    var position: Vector3fc = position
        set(value) {
            transformNeedsUpdate = true
            field = value
        }
    var direction: Vector3fc = direction
        set(value) {
            transformNeedsUpdate = true
            field = value
        }
    var fov: Float = fov
        set(value) {
            transformNeedsUpdate = true
            field = value
        }

    private var transformNeedsUpdate = true
    var localBasis: Matrix3fc = Matrix3f()
        get() {
            if(transformNeedsUpdate) updateTransform()
            return field
        }
        private set
    var perspectiveMatrix: Matrix4fc = Matrix4f()
        get() {
            if(transformNeedsUpdate) updateTransform()
            return field
        }
        private set
    var viewMatrix: Matrix4fc = Matrix4f()
        get() {
            if(transformNeedsUpdate) updateTransform()
            return field
        }
        private set
    /** Matrix that transforms vertices and fragments from world space to screen space */
    var transformMatrix: Matrix4fc = Matrix4f()
        get() {
            if(transformNeedsUpdate) updateTransform()
            return field
        }
        private set

    private fun updateTransform() {
        transformNeedsUpdate = false
        val perspTransform = Matrix4f().perspective(Math.toRadians(fov), screenSize.x.toFloat() / screenSize.y, 0.1f, 1000f)
        this.perspectiveMatrix = perspTransform

        val lookAlongMat = Matrix4f().lookAlong(direction, upVector)
        this.viewMatrix = lookAlongMat

        // Transpose because lookAlongMat is an inverted localBasis (And transpose=inverse for orthogonal matrix)
        localBasis = lookAlongMat.get3x3(Matrix3f()).transpose()

        // Final transform goes from world space (input vector) -> camera space (lookAlongMat) -> screen space (perspTransform)
        transformMatrix = perspTransform.mul(lookAlongMat).translate(Vector3f(position).negate())
    }

    fun getScreenPos(localPos: Vector4f, transform: Transform): Vector3f {
        return getScreenPos(transform.globalTransformMatrix.transform(localPos))
    }

    fun getScreenPos(globalPos: Vector4f): Vector3f {
        val pos = this.transformMatrix.transform(globalPos)
        return Vector3f(pos.x, pos.y, pos.z)
    }

    fun getScreenPos(globalPos: Vector3f) = getScreenPos(Vector4f(globalPos, 1f))

    fun getScreenPos(localPos: Vector3f, transform: Transform) = getScreenPos(Vector4f(localPos, 1f), transform)

    fun setDirection(yaw: Float, pitch: Float) {
        assert(-PI/2 <= pitch && pitch <= PI/2)
        direction = Vector3f(
            Math.cos(yaw) * Math.cos(pitch),
            Math.sin(pitch),
            Math.sin(yaw) * Math.cos(pitch)
        )
    }

    fun screenToWorld(window: Window, screenCoords: Vector2ic, normZ: Float): Vector3f {
        val size = window.size
        val inv = transformMatrix.invert(Matrix4f())
        val normScreenCoords = Vector4f(
            screenCoords.x() / size.x.toFloat() * 2f -1f,
            screenCoords.y() / size.y.toFloat() * -2f + 1f,
            normZ,
            1f
        )
        inv.transform(normScreenCoords)
        val v = normScreenCoords.div(normScreenCoords.w)
        val v3 = Vector3f(v.x, v.y, v.z)
        return v3
    }

    fun move(forwardSpeed: Float, sideSpeed: Float) {
        position = Vector3f(position).add(localBasis.transform(Vector3f(sideSpeed, 0f, forwardSpeed)))
    }
}