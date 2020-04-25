#version 330 core
out vec4 FragColor;

in vec2 TexCoord;

uniform sampler2D screenTexture;
uniform vec2 windowSize;
uniform int pixelSize;
uniform int colorBits;

void main()
{
    float dx = pixelSize / windowSize.x;
    float dy = pixelSize / windowSize.y;
    vec2 newCoords = vec2(dx * floor(TexCoord.x / dx), dy * floor(TexCoord.y / dy));
    vec4 color = texture(screenTexture, newCoords);
    FragColor = vec4(round(color.r * colorBits) / colorBits, round(color.g * colorBits) / colorBits, round(color.b * colorBits) / colorBits, color.a);
}