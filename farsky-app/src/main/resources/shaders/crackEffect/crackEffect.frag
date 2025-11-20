#version 110

uniform sampler2D screen;
uniform sampler2D crack;

uniform float percent;

void main(){
	vec4 finalColor;
	vec4 crackNormal = texture2D(crack,  gl_TexCoord[1].st);
	vec2 screenCoord = gl_TexCoord[0].st + vec2(crackNormal.r-0.5, crackNormal.g-0.5)*0.1*percent;
	
	if (screenCoord.s>1.0) screenCoord.s=1.0-(screenCoord.s-1.0);
	if (screenCoord.s<0.0) screenCoord.s=-screenCoord.s;
	if (screenCoord.t>1.0) screenCoord.t=1.0-(screenCoord.t-1.0);
	if (screenCoord.t<0.0) screenCoord.t=-screenCoord.t;
	
	finalColor = texture2D(screen,  screenCoord);
	gl_FragColor = finalColor;
}