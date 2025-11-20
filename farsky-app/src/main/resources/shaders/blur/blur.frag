#version 110

uniform sampler2D texture;
uniform float blurSize;
uniform bool vertical;
uniform float blackAndWhitePercent;
uniform float luminosity;

void main(){
	vec4 sum = vec4(0.0);
	
	// take nine samples, with the distance blurSize between them
	if (vertical){
		// blur in y (vertical)
		sum += texture2D(texture, vec2(gl_TexCoord[0].s, gl_TexCoord[0].t - 4.0*blurSize)) * 0.05;
		sum += texture2D(texture, vec2(gl_TexCoord[0].s, gl_TexCoord[0].t - 3.0*blurSize)) * 0.09;
		sum += texture2D(texture, vec2(gl_TexCoord[0].s, gl_TexCoord[0].t - 2.0*blurSize)) * 0.12;
		sum += texture2D(texture, vec2(gl_TexCoord[0].s, gl_TexCoord[0].t - blurSize)) * 0.15;
		sum += texture2D(texture, vec2(gl_TexCoord[0].s, gl_TexCoord[0].t)) * 0.16;
		sum += texture2D(texture, vec2(gl_TexCoord[0].s, gl_TexCoord[0].t + blurSize)) * 0.15;
		sum += texture2D(texture, vec2(gl_TexCoord[0].s, gl_TexCoord[0].t + 2.0*blurSize)) * 0.12;
		sum += texture2D(texture, vec2(gl_TexCoord[0].s, gl_TexCoord[0].t + 3.0*blurSize)) * 0.09;
		sum += texture2D(texture, vec2(gl_TexCoord[0].s, gl_TexCoord[0].t + 4.0*blurSize)) * 0.05;
 
	}
	else{
		// blur in x (horizontal)
		sum += texture2D(texture, vec2(gl_TexCoord[0].s - 4.0*blurSize, 	gl_TexCoord[0].t)) * 0.05;
		sum += texture2D(texture, vec2(gl_TexCoord[0].s - 3.0*blurSize, 	gl_TexCoord[0].t)) * 0.09;
		sum += texture2D(texture, vec2(gl_TexCoord[0].s - 2.0*blurSize, 	gl_TexCoord[0].t)) * 0.12;
		sum += texture2D(texture, vec2(gl_TexCoord[0].s - blurSize, 			gl_TexCoord[0].t)) * 0.15;
		sum += texture2D(texture, vec2(gl_TexCoord[0].s, 							gl_TexCoord[0].t)) * 0.16;
		sum += texture2D(texture, vec2(gl_TexCoord[0].s + blurSize, 			gl_TexCoord[0].t)) * 0.15;
		sum += texture2D(texture, vec2(gl_TexCoord[0].s + 2.0*blurSize, 	gl_TexCoord[0].t)) * 0.12;
		sum += texture2D(texture, vec2(gl_TexCoord[0].s + 3.0*blurSize, 	gl_TexCoord[0].t)) * 0.09;
		sum += texture2D(texture, vec2(gl_TexCoord[0].s + 4.0*blurSize, 	gl_TexCoord[0].t)) * 0.05;
	}
	
	float gray = (sum.r+sum.g+sum.b)/3.0*luminosity;
	sum = sum * (1.0-blackAndWhitePercent) + vec4(gray, gray, gray, 1.0) * blackAndWhitePercent;

	gl_FragColor = sum;
}