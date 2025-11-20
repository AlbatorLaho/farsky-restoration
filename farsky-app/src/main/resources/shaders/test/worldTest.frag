#version 110

// Not used, but fixes Intel HD 4000 crashing issue
uniform sampler2D texture;

void main(){
	gl_FragColor = vec4(1,1,1,0.1);
}