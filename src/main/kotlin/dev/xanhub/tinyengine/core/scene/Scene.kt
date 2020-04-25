package dev.xanhub.tinyengine.core.scene

import dev.xanhub.tinyengine.core.gameobject.GameObject
import dev.xanhub.tinyengine.renderer.Drawable
import dev.xanhub.tinyengine.renderer.Window
import dev.xanhub.tinyengine.renderer.camera.YawPitchCamera
import dev.xanhub.tinyengine.renderer.framebuffer.FrameBuffer
import dev.xanhub.tinyengine.renderer.light.DirectionalLight
import dev.xanhub.tinyengine.renderer.light.PointLight
import dev.xanhub.tinyengine.renderer.light.Spotlight
import dev.xanhub.tinyengine.renderer.math.Transformable
import dev.xanhub.tinyengine.renderer.mesh.FullscreenMesh2D
import dev.xanhub.tinyengine.renderer.shader.PostProcessor
import dev.xanhub.tinyengine.renderer.shader.Shader
import org.lwjgl.opengl.GL11.*
import kotlin.math.min

private var nextId = Int.MIN_VALUE

open class Scene(var camera: YawPitchCamera): Drawable {
    private var uniformBuffer: SceneUniformBuffer? = null

    private var postProcessorFramebuffer: FrameBuffer? = null

    private val id = nextId++

    val gameObjects: List<GameObject> = ArrayList()

    var pointLights = ArrayList<PointLight>(6)

    var directionalLight: DirectionalLight? = null

    var spotlight: Spotlight? = null

    var postProcessor: PostProcessor? = null

    final override fun draw(window: Window) {
        updateUniformBufferIfExists()

        if(postProcessorFramebuffer == null || postProcessorFramebuffer!!.size != window.size) {
            postProcessorFramebuffer?.destroy()
            postProcessorFramebuffer = FrameBuffer(window.size)
        }

        sortGameObjects()
        if(postProcessor == null) {
            glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
            for(model in gameObjects) {
                model.draw(window)
            }
        }
        else {
            postProcessorFramebuffer!!.use {
                glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
                drawModels(window)
            }

            postProcess(window, postProcessor!!)

            glDisable(GL_DEPTH_TEST)
            drawGUI(window)
            glEnable(GL_DEPTH_TEST)
        }
    }

    /**
     * Draw 3D models here. Depth testing is enabled here.
     */
    open fun drawModels(window: Window) {
        for(model in gameObjects) {
            model.draw(window)
        }
    }

    /**
     * Function that draws GUI elements. No depth testing is done with anything
     * drawn in this function, unless manually overwritten with glEnable(GL_DEPTH_TEST).
     */
    open fun drawGUI(window: Window) {
        // TODO: Put stuff here when UI is added.
    }

    private fun postProcess(window: Window, processor: PostProcessor) {
        glDisable(GL_DEPTH_TEST)
        FullscreenMesh2D.draw(window, this, processor, postProcessorFramebuffer!!)
        glEnable(GL_DEPTH_TEST)
    }

    fun getUniformBuffer(shader: Shader): SceneUniformBuffer {
        if(this.uniformBuffer == null) {
            this.uniformBuffer = SceneUniformBuffer(
                    shader,
                    camera.transformMatrix,
                    camera.position
            )
            updateUniformBufferIfExists()
        }
        return this.uniformBuffer!!
    }

    private fun updateUniformBufferIfExists() {
        this.uniformBuffer?.apply {
            setDirectionalLight(directionalLight)
            setSpotlight(spotlight)
            for(i in 0 until 6)
                setPointLight(i, pointLights.getOrNull(i))
            setNumLights(min(pointLights.size, 6))
            setCameraPos(camera.position)
            setCameraTransform(camera.transformMatrix)
        }
    }

    private fun sortGameObjects() {
        gameObjects as ArrayList
        fun distanceToCameraSquared(transformable: Transformable): Float =
            transformable.transform.localTranslation.distanceSquared(camera.position)
        for(i in 0 until gameObjects.size - 1) {
            val gameObj = gameObjects[i]
            if(!gameObj.material.isTranslucent) continue

            var j = i
            val d = distanceToCameraSquared(gameObj.transform)
            while(
                j + 1 < gameObjects.size &&
                (!gameObjects[j + 1].material.isTranslucent ||
                    distanceToCameraSquared(gameObjects[j + 1].transform) > d)
            ) {
                val temp = gameObjects[j + 1]
                gameObjects[j + 1] = gameObj
                gameObjects[j] = temp
                j++
            }
        }
    }

    override fun equals(other: Any?): Boolean = this === other

    override fun hashCode(): Int = id
}