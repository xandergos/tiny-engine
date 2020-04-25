package dev.xanhub.tinyengine.renderer.mesh

import dev.xanhub.tinyengine.util.fromAssimp
import org.joml.Vector2f
import org.joml.Vector3f
import org.joml.Vector3i
import org.lwjgl.assimp.AIMesh
import org.lwjgl.assimp.Assimp
import java.io.File

object MeshLoader {
    val requiredFlags = Assimp.aiProcess_Triangulate or Assimp.aiProcess_GenNormals
    var defaultFlags = Assimp.aiProcess_CalcTangentSpace or
            Assimp.aiProcess_DropNormals

    fun loadManyMinimal(file: File, flags: Int = defaultFlags): List<Mesh<MinimalVertexBuffer>> {
        val aiScene = Assimp.aiImportFile(file.path, flags or requiredFlags) ?: return emptyList()
        val aiMeshes = aiScene.mMeshes() ?: return emptyList()

        val meshes = ArrayList<Mesh<MinimalVertexBuffer>>()

        while(aiMeshes.hasRemaining()) {
            val aiMesh = AIMesh.create(aiMeshes.get())
            meshes.add(loadMeshMinimal(aiMesh))
        }

        return meshes
    }

    fun loadSingleMinimal(file: File, flags: Int = defaultFlags): Mesh<MinimalVertexBuffer> {
        if(!file.exists()) throw IllegalStateException("File doesn't exist.")
        val aiScene = Assimp.aiImportFile(file.path, flags or requiredFlags) ?: throw IllegalStateException("Could not parse file.")
        val aiMeshes = aiScene.mMeshes() ?: throw IllegalStateException("Not exactly one mesh in scene.")

        val mesh: Mesh<MinimalVertexBuffer>

        if(aiScene.mNumMeshes() == 1) {
            val aiMesh = AIMesh.create(aiMeshes.get())
            mesh = loadMeshMinimal(aiMesh)
        }
        else throw IllegalStateException("Not exactly one mesh in scene.")

        return mesh
    }

    private fun loadMeshMinimal(aiMesh: AIMesh): Mesh<MinimalVertexBuffer> {
        val aiPositions = aiMesh.mVertices()
        val aiNormals = aiMesh.mNormals()!!
        val aiTextureCoords = aiMesh.mTextureCoords(0)!!

        val vertexBuffer = MinimalVertexBuffer(aiMesh.mNumVertices())
        while(aiPositions.hasRemaining()) {
            val texCoords = aiTextureCoords.get()
            vertexBuffer.put(
                Vector3f().fromAssimp(aiPositions.get()),
                Vector3f().fromAssimp(aiNormals.get()),
                Vector2f(texCoords.x(), texCoords.y())
            )
        }

        val faces = ArrayList<Vector3i>()

        val aiFaces = aiMesh.mFaces()
        while(aiFaces.hasRemaining()) {
            val aiFace = aiFaces.get()
            val aiIndices = aiFace.mIndices()
            faces.add(Vector3i(aiIndices.get(0), aiIndices.get(1), aiIndices.get(2)))
        }

        return Mesh(vertexBuffer, faces)
    }

    fun loadManyDetailed(file: File, flags: Int = defaultFlags): List<Mesh<DetailedVertexBuffer>> {
        val aiScene = Assimp.aiImportFile(file.path, flags or requiredFlags) ?: return emptyList()
        val aiMeshes = aiScene.mMeshes() ?: return emptyList()

        val meshes = ArrayList<Mesh<DetailedVertexBuffer>>()

        while(aiMeshes.hasRemaining()) {
            val aiMesh = AIMesh.create(aiMeshes.get())
            meshes.add(loadMeshDetailed(aiMesh))
        }

        return meshes
    }

    fun loadSingleDetailed(file: File, flags: Int = defaultFlags): Mesh<DetailedVertexBuffer> {
        if(!file.exists()) throw IllegalStateException("File doesn't exist.")
        val aiScene = Assimp.aiImportFile(file.path, flags or requiredFlags) ?: throw IllegalStateException("Could not parse file.")
        val aiMeshes = aiScene.mMeshes() ?: throw IllegalStateException("Not exactly one mesh in scene.")

        val mesh: Mesh<DetailedVertexBuffer>

        if(aiScene.mNumMeshes() == 1) {
            val aiMesh = AIMesh.create(aiMeshes.get())
            mesh = loadMeshDetailed(aiMesh)
        }
        else throw IllegalStateException("Not exactly one mesh in scene.")

        return mesh
    }

    private fun loadMeshDetailed(aiMesh: AIMesh): Mesh<DetailedVertexBuffer> {
        val aiPositions = aiMesh.mVertices()
        val aiNormals = aiMesh.mNormals()!!
        val aiTextureCoords = aiMesh.mTextureCoords(0)!!
        val aiTangents = aiMesh.mTangents()!!
        val aiBiTangents = aiMesh.mBitangents()!!

        val vertexBuffer = DetailedVertexBuffer(aiMesh.mNumVertices())
        while(aiPositions.hasRemaining()) {
            val texCoords = aiTextureCoords.get()
            vertexBuffer.put(
                Vector3f().fromAssimp(aiPositions.get()),
                Vector3f().fromAssimp(aiNormals.get()),
                Vector2f(texCoords.x(), texCoords.y()),
                Vector3f().fromAssimp(aiTangents.get()),
                Vector3f().fromAssimp(aiBiTangents.get())
            )
        }

        val faces = ArrayList<Vector3i>()

        val aiFaces = aiMesh.mFaces()
        while(aiFaces.hasRemaining()) {
            val aiFace = aiFaces.get()
            val aiIndices = aiFace.mIndices()
            faces.add(Vector3i(aiIndices.get(0), aiIndices.get(1), aiIndices.get(2)))
        }

        return Mesh(vertexBuffer, faces)
    }
}