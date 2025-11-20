#version 110

// Texture Params
uniform sampler2D texture;

uniform float colorFactor;

void main(){
	vec4 finalColor = texture2D(texture, gl_TexCoord[0].st) * gl_Color + vec4(vec3(colorFactor),0);
	gl_FragColor = finalColor;
}