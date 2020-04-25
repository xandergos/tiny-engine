package dev.xanhub.tinyengine.renderer.material

import org.lwjgl.opengl.GL11.glDeleteTextures
import org.lwjgl.opengl.GL30

abstract class Texture(val id: Int) {
    fun use(activeTexture: Int = GL30.GL_TEXTURE0) {
        GL30.glActiveTexture(activeTexture)
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, id)
    }

    fun destroy() {
        glDeleteTextures(id)
    }
}