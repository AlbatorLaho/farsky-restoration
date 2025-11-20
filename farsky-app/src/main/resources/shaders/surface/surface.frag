#version 110

// Light
uniform vec3 light_ambient;
uniform vec3 light_diffuse;
uniform vec3 light_specular;
uniform vec3 light_ambient_inside;

// Texture
uniform sampler2D texture;

// Global Light params
uniform float lightLimit;
uniform float visibleLimit;
uniform vec3 glowColor;
uniform bool directColor;
uniform bool water;

varying float d,colorLight;

vec3 seaGradientColor(vec3 color){
	return mix(glowColor, color.rgb, clamp((visibleLimit-d)/400.0, 0.1, 1.0));
}

void main(){
	vec4 finalColor = texture2D(texture, gl_TexCoord[0].st) * gl_Color;
	
	if (water) finalColor *= vec4(vec3(colorLight), 1.0);
	
	if (!directColor){
		// Global lighting
		finalColor = finalColor * vec4(light_ambient,1.0);
		
		// Light
		if (d<lightLimit){
			float light = min(lightLimit*lightLimit/(d*d), 1.5);
			finalColor = finalColor * vec4(light,light,light,1);
		}
		
		// Gradient blur
		finalColor.a = min(finalColor.a,(visibleLimit-d)/40.0);
		
		// Water Glow Color Interpolation
		finalColor.rgb = seaGradientColor(finalColor.rgb);
	}
	
	
	gl_FragColor =  finalColor;
}