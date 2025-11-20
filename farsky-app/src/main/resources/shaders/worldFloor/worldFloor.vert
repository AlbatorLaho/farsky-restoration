#version 110

uniform float time;

varying vec3 lightDir, eyeVec;
varying float d;

void main(void){
	// Tangent & Normal vector => Tangent could be given via openGL to work in every case
	vec3 tangent = cross(gl_Normal, vec3(0, 0, 1)); 
	if (tangent==vec3(0,0,0)) tangent = vec3(0.0, -1.0, 0.0);

	tangent = normalize(tangent);
	
	// Bump Mapping (change coordinates to normal coordinates)
	vec3 n = normalize(gl_NormalMatrix * gl_Normal);
	vec3 t = normalize(gl_NormalMatrix * tangent);
	vec3 b = cross(n, t);
		
	// Light
	vec3 vVertex = vec3(gl_ModelViewMatrix * gl_Vertex);
	vec3 tmpVec = vec3(/*gl_LightSource[0].position.xyz*/ - vVertex);
	lightDir.x = dot(tmpVec, t);
	lightDir.y = dot(tmpVec, b);
	lightDir.z = dot(tmpVec, n);
	
	tmpVec = -vVertex;
	eyeVec.x = dot(tmpVec, t);
	eyeVec.y = dot(tmpVec, b);
	eyeVec.z = dot(tmpVec, n);
	
	d = length(lightDir);
	
	gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
	gl_TexCoord[0] = gl_MultiTexCoord0;
	gl_TexCoord[1] = gl_MultiTexCoord0;
	gl_FrontColor=gl_Color;
	
}