#version 110

// Light
uniform vec3 light_ambient;
uniform vec3 light_diffuse;
uniform vec3 light_specular;

// Texture Params
uniform sampler2D colorTex;

// Main Params
uniform bool topLight;

// Light params
uniform float lightLimit;
uniform float visibleLimit;
uniform vec3 glowColor;
uniform bool emissive;

// Varying
varying float d;
varying vec3 N,topLightDir;

// Alpha light param
uniform float alphaLightPercent;
uniform vec3 alphaLightcolor;
uniform bool invertAlphaLight;

vec3 seaGradientColor(vec3 color){
	return mix(glowColor, color.rgb, clamp((visibleLimit-d)/400.0,0.1, 1.0));
}

void main(){
	
	vec4 finalColor = texture2D(colorTex, gl_TexCoord[0].st).rgba;
	
	if (emissive){
		finalColor.a = min(finalColor.a,(visibleLimit-d)/40.0);
	}
	else{
		
		// Ambient light
		finalColor = finalColor * vec4(light_ambient, 1.0);
		
		// ligh from top
		if (topLight) finalColor.rgb = finalColor.rgb * (dot(normalize(N),normalize(topLightDir)) / 2.5 + 1.0);
		
		// Water Glow Color Interpolation
		finalColor.rgb = seaGradientColor(finalColor.rgb);
		
		// Emissive light from alpha
		if (alphaLightPercent>0.0){
			if (invertAlphaLight){
				finalColor.rgb=finalColor.rgb+finalColor.a*alphaLightPercent*alphaLightcolor;
			}
			else{
				finalColor.rgb=finalColor.rgb+(1.0-finalColor.a)*alphaLightPercent*alphaLightcolor;
				finalColor.a=1.0;
			}
		}
			
		// Gradient blur
		finalColor.a = min(finalColor.a,(visibleLimit-d)/40.0);
		
		// Light
		if (d<lightLimit){
			float light = min(lightLimit*lightLimit/(d*d), 2.0);
			finalColor = finalColor * vec4(light,light,light,1);
		}
	}
	
	gl_FragColor = finalColor * gl_Color.rgba;
}