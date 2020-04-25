package dev.xanhub.tinyengine.core.gameobject.sprite

import dev.xanhub.tinyengine.core.scene.Scene
import dev.xanhub.tinyengine.renderer.material.Material
import dev.xanhub.tinyengine.renderer.shader.ObjectShader

class StaticSprite(
    scene: Scene,
    material: Material,
    shader: ObjectShader,
    preDraw: Runnable? = null,
    postDraw: Runnable? = null
): Sprite(scene, material, shader, preDraw, postDraw)