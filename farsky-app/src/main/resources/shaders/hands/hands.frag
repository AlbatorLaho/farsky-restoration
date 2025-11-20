#version 110

// Texture Params
uniform sampler2D colorTex;

uniform float percent;
uniform vec3 color;

void main(){
	vec4 finalColor = vec4(texture2D(colorTex, gl_TexCoord[0].st).rgb * gl_Color.rgb, 1.0);
	float alpha = texture2D(colorTex, gl_TexCoord[0].st).a;
	
	finalColor.rgb=finalColor.rgb+(1.0-alpha)*percent*color;
	
	gl_FragColor = finalColor;
}