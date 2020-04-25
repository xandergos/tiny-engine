package dev.xanhub.tinyengine.renderer.math

import dev.xanhub.tinyengine.renderer.shader.Shader
import org.joml.Quaternionf
import org.joml.Quaternionfc
import org.joml.Vector3f
import org.joml.Vector3fc

class TransformBuffered(
    val shader: Shader,
    localTranslation: Vector3fc = Vector3f(),
    localRotation: Quaternionfc = Quaternionf(),
    localScale: Vector3fc = Vector3f(1f)
) : Transform(localTranslation, localRotation, localScale) {
    val uniformBuffer = TransformUniformBuffer(shader, this)
}