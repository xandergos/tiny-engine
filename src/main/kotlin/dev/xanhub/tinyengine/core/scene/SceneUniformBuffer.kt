package dev.xanhub.tinyengine.core.scene

import dev.xanhub.tinyengine.renderer.light.DirectionalLight
import dev.xanhub.tinyengine.renderer.light.PointLight
import dev.xanhub.tinyengine.renderer.light.Spotlight
import dev.xanhub.tinyengine.renderer.shader.Shader
import dev.xanhub.tinyengine.renderer.shader.uniformbuffer.Uniform
import dev.xanhub.tinyengine.renderer.shader.uniformbuffer.UniformBuffer
import org.joml.Matrix4fc
import org.joml.Vector3f
import org.joml.Vector3fc

const val maxLights: Int = 6
private val zero = Vector3f()

private val names = ArrayList<String>(7*maxLights + 14).apply {
    add("numLights")
    for(i in 0 until maxLights) {
        add("pointLights[$i].position")
        add("pointLights[$i].ambient")
        add("pointLights[$i].diffuse")
        add("pointLights[$i].specular")
        add("pointLights[$i].constant")
        add("pointLights[$i].linear")
        add("pointLights[$i].quadratic")
    }

    add("directionalLight.direction")
    add("directionalLight.ambient")
    add("directionalLight.diffuse")
    add("directionalLight.specular")

    add("spotlight.position")
    add("spotlight.direction")
    add("spotlight.innerAngleCosine")
    add("spotlight.outerAngleCosine")
    add("spotlight.ambient")
    add("spotlight.diffuse")
    add("spotlight.specular")

    add("cameraTransform")
    add("cameraPos")
}.toTypedArray()

class SceneUniformBuffer(
        shader: Shader,
        initialCameraTransform: Matrix4fc,
        initialCameraPos: Vector3fc
): UniformBuffer(shader, "SceneData") {
    private val uniforms = Uniform.listOf(shader, *names)

    init {
        setNumLights(0)
        setDirectionalLight(null)
        setSpotlight(null)
        setCameraTransform(initialCameraTransform)
        setCameraPos(initialCameraPos)
    }

    fun setNumLights(n: Int) = this.set(uniforms[0], n)

    fun setPointLight(i: Int, v: PointLight?) {
        val first = 1 + 7 * i
        set(uniforms[first], v?.position ?: zero)
        set(uniforms[first + 1], v?.ambient ?: zero)
        set(uniforms[first + 2], v?.diffuse ?: zero)
        set(uniforms[first + 3], v?.specular ?: zero)
        set(uniforms[first + 4], v?.c0 ?: 0f)
        set(uniforms[first + 5], v?.c1 ?: 0f)
        set(uniforms[first + 6], v?.c2 ?: 0f)
    }

    fun setDirectionalLight(v: DirectionalLight?) {
        val first = 1 + 7 * maxLights
        set(uniforms[first], v?.direction ?: zero)
        set(uniforms[first + 1], v?.ambient ?: zero)
        set(uniforms[first + 2], v?.diffuse ?: zero)
        set(uniforms[first + 3], v?.specular ?: zero)
    }

    fun setSpotlight(v: Spotlight?) {
        val first = 1 + 7 * maxLights + 4
        set(uniforms[first], v?.position ?: zero)
        set(uniforms[first + 1], v?.direction ?: zero)
        set(uniforms[first + 2], v?.innerAngleCosine ?: 1f)
        set(uniforms[first + 3], v?.outerAngleCosine ?: 1f)
        set(uniforms[first + 4], v?.ambient ?: zero)
        set(uniforms[first + 5], v?.diffuse ?: zero)
        set(uniforms[first + 6], v?.specular ?: zero)
    }

    fun setCameraTransform(m: Matrix4fc) = set(uniforms[1 + 7 * maxLights + 4 + 7], m)

    fun setCameraPos(v: Vector3fc) = set(uniforms[1 + 7 * maxLights + 4 + 7 + 1], v)
}