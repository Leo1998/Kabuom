#version 110

varying vec4 v_color;
varying vec2 v_texCoords;

uniform sampler2D sceneTexture;

uniform float time;
uniform vec2 amount;

void main() {
	vec2 texCoord = v_texCoords.xy;

	texCoord.x += sin(texCoord.y * amount.x * 3.14159 + time) / 100.0;
	texCoord.y += sin(texCoord.x * amount.y * 3.14159 + time) / 100.0;

	gl_FragColor = texture2D(sceneTexture, texCoord);
}