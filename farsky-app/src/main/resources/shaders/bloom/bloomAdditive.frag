#version 110

uniform sampler2D src;
uniform sampler2D dst;

void main(){
	vec4 src_color = texture2D(src, gl_TexCoord[0].st).rgba * gl_Color;
	vec4 dst_color = texture2D(dst, gl_TexCoord[1].st).rgba * gl_Color;
	
	gl_FragColor = (src_color+dst_color) - (src_color*dst_color);
}