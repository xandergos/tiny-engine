package dev.xanhub.tinyengine.renderer.shader

import dev.xanhub.tinyengine.core.gameobject.GameObject
import dev.xanhub.tinyengine.core.scene.Scene
import dev.xanhub.tinyengine.renderer.material.Material
import dev.xanhub.tinyengine.renderer.material.NormalMappedMaterial
import dev.xanhub.tinyengine.renderer.math.TransformBuffered
import dev.xanhub.tinyengine.renderer.mesh.Mesh
import dev.xanhub.tinyengine.renderer.mesh.MinimalVertexBuffer
import dev.xanhub.tinyengine.renderer.mesh.VertexBuffer
import dev.xanhub.tinyengine.util.Resource
import org.lwjgl.opengl.GL13.GL_TEXTURE0
import org.lwjgl.opengl.GL13.GL_TEXTURE1

object DetailedShader : MeshShader(Resource.get("shaders/detailed/vertex.glsl"), Resource.get("shaders/detailed/fragment.glsl")) {
    override fun updateUniforms(scene: Scene, mesh: Mesh<out VertexBuffer>, material: Material, transform: TransformBuffered) {
        if(material !is NormalMappedMaterial)
            throw IllegalArgumentException("Material must be normal mapped to use DetailedShader.")
        // Material
        this.setUniformInt("material.diffuse", 0)
        material.diffuse.use(GL_TEXTURE0)
        this.setUniformVec3f("material.specular", material.specularColor)
        this.setUniformFloat("material.shininess", material.shininess)
        this.setUniformInt("material.normalMap", 1)
        material.normalMap.use(GL_TEXTURE1)

        if(mesh.vertexBuffer is MinimalVertexBuffer) throw IllegalArgumentException()
        this.setUniformBlock("ModelData", transform.uniformBuffer, 0)
        this.setUniformBlock("SceneData", scene.getUniformBuffer(this), 1)
    }

    override fun updateUniforms(scene: Scene, gameObject: GameObject) {
        updateUniforms(scene, gameObject.mesh, gameObject.material, gameObject.transform)
    }
}