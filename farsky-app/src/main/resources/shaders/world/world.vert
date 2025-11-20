#version 110

uniform float time;

// Main Params
uniform bool directColor;
uniform bool topLight;
uniform bool emissive;

// Wave variables
uniform bool wave,useAlphaAsHeight,Ywave,Xwave;
uniform float height,factor,offset,amplitude;

// Light params
uniform vec3 topLightPos;
varying float d;
varying vec3 N,topLightDir;

void main(void){

	vec4 pos;
	vec4 vertex;
	
	gl_TexCoord[0] = gl_MultiTexCoord0;
	gl_FrontColor=gl_Color;
	
	// Water waves on objects. Rotation => color.a = rot / 1000.0 in radians
	vertex = gl_Vertex;
	if (wave){
		float H;
		if (useAlphaAsHeight) H = gl_Color.a / height;
		else H = vertex.y / height;
		
		float waveFact = cos(time/2000.0*factor - H * 3.1415 * 2.0 + offset);
		vertex.x = vertex.x + H * H * waveFact * amplitude * cos(gl_Color.a * 1000.0);
		vertex.z = vertex.z + H * H * waveFact * amplitude * sin(gl_Color.a * 1000.0);
	}
	
	pos = gl_ModelViewProjectionMatrix * vertex;
	

	// Only do d computation
	vec3 vVertex = vec3(gl_ModelViewMatrix * vertex);
	d = length(/*gl_LightSource[0].position.xyz - */vVertex);
	
	if (topLight){
		N = gl_NormalMatrix * gl_Normal;
		topLightDir = topLightPos - vVertex;
		topLightDir = normalize(topLightDir);
	}
	
	gl_Position = pos;
}