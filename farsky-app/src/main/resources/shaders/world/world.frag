#version 110

// Light
uniform vec3 light_ambient;
uniform vec3 light_diffuse;
uniform vec3 light_specular;

// Texture Params
uniform sampler2D colorTex;

// Main Params
uniform bool directColor;
uniform bool topLight;
uniform bool emissive;
uniform bool highlight;

// Light params
uniform float lightLimit;
uniform float visibleLimit;
uniform vec3 glowColor;

// Alpha light param
uniform float alphaLightPercent;
uniform vec3 alphaLightcolor;
uniform bool invertAlphaLight;

// Wave
uniform bool useAlphaAsHeight;

// Other Params
uniform bool discardTransparency,selected;
uniform float selectedFactor;

// Varying
varying float d;
varying vec3 N,topLightDir;

vec3 seaGradientColor(vec3 color){
	return mix(glowColor, color.rgb, clamp((visibleLimit-d)/400.0, 0.1, 1.0));
}

void main(){
	
	vec4 finalColor = vec4(0,0,0,0);
	vec4 color = gl_Color;
	if (useAlphaAsHeight) color.a = 1.0;
	

	if (directColor || emissive){
		finalColor = texture2D(colorTex, gl_TexCoord[0].st).rgba * color;
		
		// Gradient blur
		if (emissive) finalColor.a = min(finalColor.a,(visibleLimit-d)/40.0);
	}
	else{
		finalColor = texture2D(colorTex, gl_TexCoord[0].st).rgba * color;
		
		if (discardTransparency && finalColor.a==0.0){
			discard;
			return;
		}
		
		// Light from avatar
		if (d<lightLimit){
			float light = min(lightLimit*lightLimit/(d*d), 3.0);
			if (highlight) light=max(light*0.5, 1.0);
			finalColor = finalColor * vec4(light, light, light, 1.0);
		}
		
		// ligh from top
		if (topLight){
			finalColor.rgb = finalColor.rgb * (dot(normalize(N),normalize(topLightDir)) / 1.5 + 1.0);
		}
			
		if (!highlight){
			finalColor = finalColor * vec4(light_ambient,1.0);
		}
		
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
		
		// Selection
		if (selected) finalColor.rgb = finalColor.rgb * selectedFactor;
		
		// Gradient blur
		finalColor.a = min(finalColor.a,(visibleLimit-d)/40.0);
	}
	
	gl_FragColor = finalColor;
	
	
}