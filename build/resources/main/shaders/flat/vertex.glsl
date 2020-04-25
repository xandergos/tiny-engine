#version 330 core
layout (location = 0) in vec3 aPos;

uniform mat4 cameraTransform;
uniform mat4 modelTransform;
uniform vec3 color;

void main()
{
    gl_Position = cameraTransform * modelTransform * vec4(aPos, 1.0);
}