#version 110

uniform float time;

// Main Params
uniform bool topLight;

// Wave variables
//axisSign = (1,1,1) => no sign consideration
//axisSign = (-1,-1,-1) => sign consideration
uniform float height,factor,offset;
uniform vec3 axis,wave,axisSign;

// Light params
uniform vec3 topLightPos;
varying float d;
varying vec3 N,topLightDir;

void main(void){

	vec4 pos;
	vec4 vertex;
	vec3 signFactor;
	
	gl_TexCoord[0] = gl_MultiTexCoord0;
	gl_FrontColor=gl_Color;
	
	// Water waves on objects
	vertex = gl_Vertex;

	float H,waveFact;
	
	// Wave operation
	H = abs(dot(vertex.xyz, axis)) / height;
	waveFact = cos(time/2000.0*factor - H * 3.1415 * 2.0 + offset);
	signFactor = vec3(max(sign(vertex.x), axisSign.x), max(sign(vertex.y), axisSign.y), max(sign(vertex.z), axisSign.z));
	vertex.xyz = vertex.xyz +  (H * H * waveFact * 2.0) * wave * signFactor;
	
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