#version 110

uniform sampler2D texture;
uniform float brightPassLevel;

void main(){
	vec4 color = texture2D(texture, gl_TexCoord[0].st).rgba * gl_Color;
	float brightness = (0.2126*color.r) + (0.7152*color.g) + (0.0722*color.b);//(color.r+color.g+color.b)/3.0;
	
	if (brightness<brightPassLevel) color.rgb = vec3(0.0,0.0,0.0);
	else{
		float middle = (1.0+brightPassLevel)/2.0;
		color.rgb = (color.rgb - middle)*0.5/(1.0-middle)+0.5;
	}
	
	gl_FragColor = color;
}