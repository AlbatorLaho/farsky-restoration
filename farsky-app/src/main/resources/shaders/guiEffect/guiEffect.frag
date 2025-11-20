#version 110

uniform sampler2D texture;
uniform bool blackAndWhite;
uniform float additiveLight;

void main(){
	vec4 finalColor = texture2D(texture, gl_TexCoord[0].st) * gl_Color;
	
	if (blackAndWhite) finalColor.rgb = vec3((finalColor.r + finalColor.g + finalColor.b)/3.0);
	finalColor.rgb += vec3(additiveLight);
	
	gl_FragColor = finalColor;
}