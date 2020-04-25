#version 330 core

out vec4 FragColor;

uniform mat4 cameraTransform;
uniform mat4 modelTransform;
uniform vec3 color;

void main()
{
    FragColor = vec4(color, 1f);
}