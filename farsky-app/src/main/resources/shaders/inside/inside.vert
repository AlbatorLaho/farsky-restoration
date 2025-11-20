#version 110

uniform float time;
uniform bool water,toplight;

varying float d;
varying vec3 N;

void main(){
	vec4 vertex = gl_Vertex;
	vec4 color = gl_Color;
	
	if (water){
		vertex.y += cos(time/410.0+vertex.x*3.1415*1.0+vertex.z*3.1415*1.0) * 0.05;
		vertex.y += cos(time/535.0+vertex.z*3.1415*1.0+3.1415*0.5) * 0.05;
		color += vec4(1,1,1,0)*vertex.y*1.0;
	}
	
	// Only do d computation
	vec3 vVertex = vec3(gl_ModelViewMatrix * vertex);
	d = length(/*gl_LightSource[0].position.xyz - */vVertex);
	
	if (toplight){
		N = gl_Normal;
	}
	
	gl_Position = gl_ModelViewProjectionMatrix * vertex;
	gl_FrontColor = color;
	gl_TexCoord[0] = gl_MultiTexCoord0;
}