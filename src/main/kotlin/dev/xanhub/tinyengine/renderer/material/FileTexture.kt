package dev.xanhub.tinyengine.renderer.material

import org.lwjgl.opengl.GL30.*
import org.lwjgl.stb.STBImage.*
import org.lwjgl.system.MemoryStack.stackPush
import java.io.File
import java.lang.ref.WeakReference
import java.nio.ByteBuffer

/**
 * Utility class for creating 2D OpenGL textures.
 *
 * @param data: Texture data
 * @param sWrapping: Type of wrapping along x axis
 * @param tWrapping: Type of wrapping along y axis
 * @param minFilter: Filter used when img is minimized (# of pixels using texture < size of texture)
 * @param magFilter: Filter used when img is magnified (# of pixels using texture > size of texture)
 * @param useMipmaps: Whether or not to use mipmaps
 */
class FileTexture private constructor(
    data: TextureData,
    sWrapping: Int,
    tWrapping: Int,
    minFilter: Int,
    magFilter: Int,
    useMipmaps: Boolean
): Texture(glGenTextures()) {
    data class TextureData(val width: Int, val height: Int, val numChannels: Int, val data: ByteBuffer)

    companion object {
        private val textures = HashMap<String, WeakReference<FileTexture>>()

        fun get(
            path: String,
            sWrapping: Int = GL_REPEAT,
            tWrapping: Int = GL_REPEAT,
            minFilter: Int = GL_NEAREST_MIPMAP_NEAREST,
            magFilter: Int = GL_NEAREST,
            useMipmaps: Boolean = true
        ): FileTexture {
            var v = textures[path]?.get()
            if(v == null) {
                v = FileTexture(
                    loadData(
                        File(path)
                    ), sWrapping, tWrapping, minFilter, magFilter, useMipmaps
                )
                textures[path] = WeakReference(v)
            }
            return v
        }

        fun get(
                file: File,
                sWrapping: Int = GL_REPEAT,
                tWrapping: Int = GL_REPEAT,
                minFilter: Int = GL_NEAREST_MIPMAP_NEAREST,
                magFilter: Int = GL_NEAREST,
                useMipmaps: Boolean = true
        ): FileTexture {
            var v = textures[file.absolutePath]?.get()
            if(v == null) {
                v = FileTexture(loadData(file), sWrapping, tWrapping, minFilter, magFilter, useMipmaps)
                textures[file.absolutePath] = WeakReference(v)
            }
            return v
        }

        fun loadData(imageFile: File): TextureData {
            stbi_set_flip_vertically_on_load(true)
            var width: Int? = null
            var height: Int? = null
            var nrChannels: Int? = null
            var data: ByteBuffer? = null
            stackPush().use {stack ->
                val widthBuff = stack.mallocInt(1)
                val heightBuff = stack.mallocInt(1)
                val nrChannelsBuff = stack.mallocInt(1)
                data = stbi_load(imageFile.absolutePath, widthBuff, heightBuff, nrChannelsBuff, 0)
                width = widthBuff.get(0)
                height = heightBuff.get(0)
                nrChannels = nrChannelsBuff.get(0)
            }
            if(data == null)
                throw Exception("Texture failed to load.")
            return TextureData(
                width!!,
                height!!,
                nrChannels!!,
                data!!
            )
        }
    }

    var data: TextureData
        private set

    init {
        glBindTexture(GL_TEXTURE_2D, id)

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, sWrapping)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, tWrapping)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, minFilter)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, magFilter)

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, data.width, data.height, 0, GL_RGBA, GL_UNSIGNED_BYTE, data.data)
        if(useMipmaps)
            glGenerateMipmap(GL_TEXTURE_2D)

        this.data = data

        stbi_image_free(data.data)
    }
}