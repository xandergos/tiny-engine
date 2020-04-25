#version 330 core

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

uniform struct Material {
    sampler2D diffuse;
    vec3 specular;
    float shininess;
    sampler2D normalMap;
} material;


out vec4 FragColor;

in vec3 FragPos;
in vec2 TexCoord;
in mat3 TBN;

vec3 directionalStrength();
vec3 pointStrength(int i);
vec3 spotlightStrength();

vec3 normal;

void main()
{
    normal = texture(material.normalMap, TexCoord).rgb;
    normal = normal * 2.0 - 1.0;
    normal = normalize(TBN * normal);

    vec3 directional = directionalStrength();
    vec3 pointSum = vec3(0);
    for(int i = 0; i < numLights; i++)
        pointSum = pointSum + pointStrength(i);
    vec3 spotlight = spotlightStrength();
    FragColor = vec4(directional + pointSum + spotlight, texture(material.diffuse, TexCoord).w);
}

vec3 spotlightStrength()
{
    vec3 lightDir = normalize(spotlight.position - FragPos);
    float theta = dot(lightDir, normalize(-spotlight.direction));

    if(theta > spotlight.outerAngleCosine)
    {
        // ambient
        vec3 ambient = spotlight.ambient * vec3(texture(material.diffuse, TexCoord));

        // diffuse
        float diff = max(dot(normal, lightDir), 0.0);
        vec3 diffuse = spotlight.diffuse * diff * vec3(texture(material.diffuse, TexCoord));

        // specular
        vec3 viewDir = normalize(cameraPos - FragPos);
        vec3 reflectDir = reflect(-lightDir, normal);
        float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);
        vec3 specular = spotlight.specular * (spec * material.specular);

        float epsilon = spotlight.innerAngleCosine - spotlight.outerAngleCosine;
        float intensity = clamp((theta - spotlight.outerAngleCosine) / epsilon, 0.0, 1.0);

        vec3 fragSourceOffset = FragPos - spotlight.position;
        float attenuation = 1f;//1/(1+pow(dot(fragSourceOffset, fragSourceOffset), 2)/10000000.0f);

        return (ambient + (diffuse + specular) * intensity) * attenuation;
    }
    else return spotlight.ambient * vec3(texture(material.diffuse, TexCoord));
}

vec3 pointStrength(int i)
{
    PointLight pointLight = pointLights[i];
    vec3 lightDir = normalize(FragPos - pointLight.position);
    // ambient
    vec3 ambient = pointLight.ambient * vec3(texture(material.diffuse, TexCoord));

    // diffuse
    float diff = max(dot(normal, -lightDir), 0.0);
    vec3 diffuse = pointLight.diffuse * diff * vec3(texture(material.diffuse, TexCoord));

    // specular
    vec3 viewDir = normalize(cameraPos - FragPos);
    vec3 reflectDir = reflect(lightDir, normal);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);
    vec3 specular = pointLight.specular * (spec * material.specular);

    float distance    = length(pointLight.position - FragPos);
    float attenuation = 1.0 / (pointLight.constant + pointLight.linear * distance +
    pointLight.quadratic * (distance * distance));

    return (ambient + diffuse + specular) * attenuation;
}

vec3 directionalStrength()
{
    // ambient
    float ambientStrength = max(dot(normal, TBN[2]), 0.0);
    vec3 ambient = directionalLight.ambient * vec3(texture(material.diffuse, TexCoord));

    // diffuse
    float diff = max(dot(normal, -directionalLight.direction), 0.0);
    vec3 diffuse = directionalLight.diffuse * diff * vec3(texture(material.diffuse, TexCoord));

    // specular
    vec3 viewDir = normalize(cameraPos - FragPos);
    vec3 reflectDir = reflect(directionalLight.direction, normal);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);
    vec3 specular = directionalLight.specular * (spec * material.specular);

    return ambient + diffuse + specular;
}