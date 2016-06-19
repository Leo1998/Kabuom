#version 110

varying vec4 v_color;
varying vec2 v_texCoords;

uniform sampler2D sceneTexture;

uniform float time;

void main() {
	vec2 texCoord = v_texCoords.xy;

    float x = texCoord.x;
	texCoord.x += sin(texCoord.y * 12.0 * 3.14159 + time) / 100.0;
	texCoord.y += sin(x * 8.0 * 3.14159 + time) / 100.0;

	gl_FragColor = texture2D(sceneTexture, texCoord);
}