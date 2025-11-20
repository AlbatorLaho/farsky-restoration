#version 110

uniform sampler2D texture;

void main(){
	vec4 finalColor;
	vec2 texCoord = gl_TexCoord[0].st;
	
	// Circle
	float factor = pow( pow(abs(texCoord.s-0.5)*2.0, 2.0) + pow(abs(texCoord.t-0.5)*2.0, 2.0), 0.5);
	// Borders
	factor = factor + max(abs(texCoord.s-0.5)*2.0, abs(texCoord.t-0.5)*2.0);
	// Divide because of addition
	factor = factor/2.0;
	// Enlarge visibility
	factor = max(0.0, (factor-0.5)/0.65);
	factor = pow(factor,2.0);
	
	vec4 color = vec4(0.0, 0.0, 0.0, 1.0);
	finalColor = texture2D(texture, gl_TexCoord[0].st) * (1.0-factor) + color * factor;
	gl_FragColor = finalColor * gl_Color;
}