#version 330 core
layout (location = 0) in vec3 aPos;
layout (location = 1) in vec3 aNormal;
layout (location = 2) in vec2 aTexCoord;
layout (location = 3) in vec3 aTangent;
layout (location = 4) in vec3 aBiTangent;

struct DirectionalLight {
    vec3 direction;
    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
};

struct PointLight {
    vec3 position;

    vec3 ambient;
    vec3 diffuse;
    vec3 specular;

    float constant;
    float linear;
    float quadratic;
};

struct Spotlight {
    vec3 position;
    vec3 direction;
    float innerAngleCosine;
    float outerAngleCosine;

    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
};

#define NUM_POINT_LIGHTS 6
uniform SceneData {
     int numLights;
     PointLight pointLights[NUM_POINT_LIGHTS];
     DirectionalLight directionalLight;
     Spotlight spotlight;
     mat4 cameraTransform;
     vec3 cameraPos;
};

uniform ModelData {
    mat4 modelTransform;
    mat4 normalTransform;
};

out vec3 FragPos;
out vec2 TexCoord;
out mat3 TBN;

void main()
{
    gl_Position = cameraTransform * modelTransform * vec4(aPos, 1.0); // see how we directly give a vec3 to vec4's constructor
    FragPos = vec3(modelTransform * vec4(aPos, 1));
    TexCoord = aTexCoord;

    vec3 normal = normalize(vec3(normalTransform * vec4(aNormal, 0)));
    vec3 tangent = normalize(vec3(normalTransform * vec4(aTangent, 0)));
    vec3 biTangent = normalize(vec3(normalTransform * vec4(aBiTangent, 0)));
    TBN = mat3(tangent, biTangent, normal);
}