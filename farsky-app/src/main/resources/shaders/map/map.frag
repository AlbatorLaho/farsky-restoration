#version 110

// Not used, but fixes Intel HD 4000 crashing issue
uniform sampler2D texture;

void main(){
	vec4 finalColor = gl_Color;
	gl_FragColor = finalColor;
}