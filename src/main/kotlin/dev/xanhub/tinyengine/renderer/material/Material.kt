package dev.xanhub.tinyengine.renderer.material

import org.joml.Vector3f

open class Material(
    val diffuse: FileTexture,
    val specularColor: Vector3f,
    val shininess: Float,
    val isTranslucent: Boolean
)