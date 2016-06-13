#version 110

varying vec4 v_color;
varying vec2 v_texCoords;

uniform sampler2D sceneTexture;

uniform float radial_blur;
uniform float radial_bright;
uniform vec2 radial_origin;
 
void main() {
	vec2 texCoord = v_texCoords.xy;

	texCoord -= radial_origin;
	vec4 color = vec4(0.0, 0.0, 0.0, 0.0);

	for (int i = 0; i < 12; i++) {
		float scale = 1.0 - radial_blur * (float(i) / 11.0);
		color += texture2D(sceneTexture, texCoord * scale + radial_origin);
	}
 
	gl_FragColor = color / 12.0 * radial_bright;
}