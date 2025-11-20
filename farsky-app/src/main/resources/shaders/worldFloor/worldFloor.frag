#version 110

// Light
uniform vec3 light_ambient;
uniform vec3 light_diffuse;
uniform vec3 light_specular;

// Texture Params
uniform sampler3D colorTex;
uniform sampler3D normalTex;
uniform sampler3D causticTex;

// caustic params
uniform bool useCaustic;
uniform float causticX;
uniform float causticY;
uniform float causticZoomX;
uniform float causticZoomY;
uniform float causticAlpha;

// Other Params
uniform float lightLimit;
uniform float visibleLimit;
uniform vec3 glowColor;
varying vec3 lightDir, eyeVec;
varying float d;

// Alpha color
uniform vec3 alphaColor;
uniform vec3 alphaAbyssColor;


vec3 seaGradientColor(vec3 color){
	return mix(glowColor, color.rgb, clamp((visibleLimit-d)/400.0,0.1, 1.0));
}

void main(){
	vec4 color = vec4(0,0,0,0);
	vec4 finalColor = vec4(0,0,0,0);
	vec3 bump = vec3(0,0,0);
	
	if (d<visibleLimit){
		if (gl_Color.r>0.0){
			color += (texture3D(colorTex, vec3(gl_TexCoord[0].st,0) + vec3(0,0,0.05)).rgba)*gl_Color.r;
			bump += (texture3D(normalTex, vec3(gl_TexCoord[1].st,0) + vec3(0,0,0.05)).rgb - 0.5)*gl_Color.r;
		}
		if (gl_Color.g>0.0){
			color += (texture3D(colorTex, vec3(gl_TexCoord[0].st,0) + vec3(0,0,0.30)).rgba)*gl_Color.g;
			bump += (texture3D(normalTex, vec3(gl_TexCoord[1].st,0) + vec3(0,0,0.30)).rgb - 0.5)*gl_Color.g;
		}
		if (gl_Color.b>0.0){
			color += (texture3D(colorTex, vec3(gl_TexCoord[0].st,0) + vec3(0,0,0.55)).rgba)*gl_Color.b;
			bump += (texture3D(normalTex, vec3(gl_TexCoord[1].st,0) + vec3(0,0,0.55)).rgb - 0.5)*gl_Color.b;
		}
		if (gl_Color.a>0.0){
			color += (texture3D(colorTex, vec3(gl_TexCoord[0].st,0) + vec3(0,0,0.80)).rgba)*gl_Color.a;
			bump += (texture3D(normalTex, vec3(gl_TexCoord[1].st,0) + vec3(0,0,0.80)).rgb - 0.5)*gl_Color.a;
		}
		
		// Light from player
		vec3 light_color = light_ambient;
		vec3 L = normalize(lightDir);
		
		if (d<lightLimit){
			vec3 N = normalize(bump);
			float falloff = min(0.04*lightLimit-0.04*d,1.0);
			
			float lambertTerm = dot(N,L);
			
			if(lambertTerm > 0.0){
				light_color += vec3(light_diffuse * lambertTerm * falloff);	
				
				vec3 E = normalize(eyeVec);
				vec3 R = reflect(-L, N);
				float specularFact = max(dot(R, E), 0.0);
				light_color += vec3(light_specular * specularFact * falloff);
			}
		}
		light_color += L.z/20.0;
		finalColor = color * vec4(light_color,1);
		
		
		// Alpha Color
		// Rock
		if (gl_Color.g>0.0){
			finalColor.rgb = finalColor.rgb + (1.0-min(finalColor.a,1.0)) * alphaColor;
			finalColor.a=1.0;
		}
		// Abyss
		if (gl_Color.a>0.0){
			finalColor.rgb = finalColor.rgb + (1.0-min(finalColor.a,1.0)) * alphaAbyssColor;
			finalColor.a=1.0;
		}
		
		// Caustic
		if (useCaustic){
			finalColor.rgb += texture3D(causticTex, vec3(gl_TexCoord[1].st, 0.25) * vec3(causticZoomX, causticZoomY,1.0) + vec3(causticX, causticY,0.0) ).rgb * causticAlpha;
			finalColor.rgb += texture3D(causticTex, vec3(gl_TexCoord[1].st, 0.75) * vec3(causticZoomX, causticZoomY,1.0) + vec3(-causticX, -causticY,0.0) ).rgb * causticAlpha;
		}
		
		
		// Gradient blur
		finalColor.a = min(finalColor.a,(visibleLimit-d)/40.0);
		
		// Water Glow Color Interpolation
		finalColor.rgb = seaGradientColor(finalColor.rgb);
	}
	
	gl_FragColor = finalColor;
}