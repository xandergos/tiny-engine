package dev.xanhub.tinyengine.util

import dev.xanhub.tinyengine.renderer.math.Transform
import org.joml.*
import org.lwjgl.assimp.AIMatrix4x4
import org.lwjgl.assimp.AIVector2D
import org.lwjgl.assimp.AIVector3D

fun Matrix4f.fromAssimp(aiMatrix4x4: AIMatrix4x4): Matrix4f {
    val t = aiMatrix4x4
    this.set(floatArrayOf(t.a1(), t.b1(), t.c1(), t.d1(),
        t.a2(), t.b2(), t.c2(), t.d2(),
        t.a3(), t.b3(), t.c3(), t.d3(),
        t.a4(), t.b4(), t.c4(), t.d4()))
    return this
}

fun Vector3f.fromAssimp(vector: AIVector3D): Vector3f {
    this.x = vector.x()
    this.y = vector.y()
    this.z = vector.z()
    return this
}

fun Vector2f.fromAssimp(vector: AIVector2D): Vector2f {
    this.x = vector.x()
    this.y = vector.y()
    return this
}

fun Vector4fc.toJavax() = javax.vecmath.Vector4f(this.x(), this.y(), this.z(), this.w())

fun Vector3fc.toJavax() = javax.vecmath.Vector3f(this.x(), this.y(), this.z())

fun Vector2fc.toJavax() = javax.vecmath.Vector2f(this.x(), this.y())

fun Matrix3fc.toJavax() = javax.vecmath.Matrix3f().apply {
    setColumn(0, this@toJavax.getColumn(0,  Vector3f()).toJavax())
    setColumn(1, this@toJavax.getColumn(1,  Vector3f()).toJavax())
    setColumn(2, this@toJavax.getColumn(2,  Vector3f()).toJavax())
}

fun Matrix4fc.toJavax() = javax.vecmath.Matrix4f().apply {
    setColumn(0, this@toJavax.getColumn(0,  Vector4f()).toJavax())
    setColumn(1, this@toJavax.getColumn(1,  Vector4f()).toJavax())
    setColumn(2, this@toJavax.getColumn(2,  Vector4f()).toJavax())
    setColumn(3, this@toJavax.getColumn(3,  Vector4f()).toJavax())
}

fun Transform.globalToBullet() = com.bulletphysics.linearmath.Transform(this.globalTransformMatrix.toJavax())

fun Transform.localToBullet() = com.bulletphysics.linearmath.Transform(this.toLocalTransformMatrix().toJavax())