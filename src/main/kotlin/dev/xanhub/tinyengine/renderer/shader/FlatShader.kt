package dev.xanhub.tinyengine.renderer.shader

import dev.xanhub.tinyengine.core.scene.Scene
import dev.xanhub.tinyengine.renderer.math.TransformBuffered
import dev.xanhub.tinyengine.util.Resource
import org.joml.Matrix4fc
import org.joml.Vector3f
import java.awt.Color

object FlatShader : Shader(Resource.get("shaders/flat/vertex.glsl"), Resource.get("shaders/flat/fragment.glsl")) {
    fun updateUniforms(
        scene: Scene,
        color: Color,
        transform: TransformBuffered
    ) {
        this.updateUniforms(
            Vector3f(color.red / 255f, color.green / 255f, color.blue / 255f),
            scene.camera.transformMatrix,
            transform.globalTransformMatrix
        )
    }

    fun updateUniforms(
        color: Vector3f,
        cameraTransform: Matrix4fc,
        modelTransform: Matrix4fc
    ) {
        this.setUniformVec3f("color", color)
        this.setUniformMat4f("cameraTransform", cameraTransform)
        this.setUniformMat4f("modelTransform", modelTransform)
    }
}