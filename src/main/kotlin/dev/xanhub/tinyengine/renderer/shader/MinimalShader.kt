package dev.xanhub.tinyengine.renderer.shader

import dev.xanhub.tinyengine.core.gameobject.GameObject
import dev.xanhub.tinyengine.core.scene.Scene
import dev.xanhub.tinyengine.renderer.material.Material
import dev.xanhub.tinyengine.renderer.math.TransformBuffered
import dev.xanhub.tinyengine.renderer.mesh.Mesh
import dev.xanhub.tinyengine.renderer.mesh.VertexBuffer
import dev.xanhub.tinyengine.util.Resource
import org.lwjgl.opengl.GL13.GL_TEXTURE0
import org.lwjgl.opengl.GL13.glActiveTexture

object MinimalShader : MeshShader(Resource.get("shaders/minimal/vertex.glsl"), Resource.get("shaders/minimal/fragment.glsl")) {
    override fun updateUniforms(
        scene: Scene,
        mesh: Mesh<out VertexBuffer>,
        material: Material,
        transform: TransformBuffered
    ) {
        // Material
        glActiveTexture(GL_TEXTURE0)
        this.setUniformInt("material.diffuse", 0)
        material.diffuse.use()
        this.setUniformVec3f("material.specular", material.specularColor)
        this.setUniformFloat("material.shininess", material.shininess)

        this.setUniformBlock("ModelData", transform.uniformBuffer, 0)
        this.setUniformBlock("SceneData", scene.getUniformBuffer(this), 1)
    }

    override fun updateUniforms(scene: Scene, gameObject: GameObject) {
        updateUniforms(scene, gameObject.mesh, gameObject.material, gameObject.transform)
    }
}