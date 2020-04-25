package dev.xanhub.tinyengine.renderer.material

import org.joml.Vector3f

class NormalMappedMaterial(
    diffuse: FileTexture,
    specularColor: Vector3f,
    shininess: Float,
    val normalMap: FileTexture,
    isTranslucent: Boolean
) : Material(diffuse, specularColor, shininess, isTranslucent)