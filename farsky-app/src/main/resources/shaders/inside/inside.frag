#version 110

// Light
uniform vec3 light_ambient;
uniform vec3 light_diffuse;
uniform vec3 light_specular;
uniform vec3 light_ambient_inside;

// Texture
uniform sampler2D texture;

// Emissive light from object
uniform bool alphaLight;
uniform vec3 lightColor;

// Global Light params
uniform float lightLimit;
uniform float visibleLimit;
uniform vec3 glowColor;
uniform bool emissive;

// Inside vessel param
uniform bool inside,discardTransparency,selected,toplight;
uniform float selectedFactor;

varying float d;
varying vec3 N;

vec3 seaGradientColor(vec3 color){
	return mix(glowColor, color.rgb, clamp((visibleLimit-d)/400.0, 0.1, 1.0));
}

void main(){
	vec4 finalColor = texture2D(texture, gl_TexCoord[0].st);
	
	// Global lighting
	if (!emissive){
		if (!inside) finalColor = finalColor * vec4(light_ambient,1.0);
		else finalColor = finalColor * vec4(light_ambient_inside, 1.0);
	}
	
	if (discardTransparency && finalColor.a==0.0){
		discard;
		return;
	}
	
	
	// Light
	if (!emissive){
		float limit = lightLimit;
		if (inside) limit=30.0;
		if (d<limit){
			float light = min(limit*limit/(d*d), 1.5);
			finalColor = finalColor * vec4(light,light,light,1);
		}
	}
	
	if (toplight){
		finalColor.rgb = finalColor.rgb * (dot(normalize(N),vec3(0,1,0))*0.5+0.75);
	}
	
	// Alpha light effect
	if (alphaLight){
		finalColor.rgb *= gl_Color.rgb;
		finalColor.rgb += lightColor*vec3(1.0-finalColor.a);
		finalColor.a = 1.0;
	}
	else{
		finalColor *= gl_Color;
	}
	
	// Selection
	if (selected) finalColor.rgb = finalColor.rgb * selectedFactor;
	
	// Gradient blur
	finalColor.a = min(finalColor.a,(visibleLimit-d)/40.0);
	// Water Glow Color Interpolation
	if (!inside && !emissive) finalColor.rgb = seaGradientColor(finalColor.rgb);
	
	gl_FragColor =  finalColor;
}