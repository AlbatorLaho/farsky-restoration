#version 110

uniform float time;
uniform bool water;
uniform float smallWaveCosFactorX, smallWaveCosFactorZ, smallWaveCosTimeDiv, smallWaveCosGain;
uniform float smallWaveSinFactorX, smallWaveSinFactorZ, smallWaveSinTimeDiv, smallWaveSinGain;
uniform float bigWaveCosFactorX, bigWaveCosFactorZ, bigWaveCosTimeDiv, bigWaveCosGain;
uniform float bigWaveSinFactorX, bigWaveSinFactorZ, bigWaveSinTimeDiv, bigWaveSinGain;
uniform float lightFactor;
uniform float xOffset,zOffset;

varying float d,colorLight;

void main(){
	vec4 vertex = gl_Vertex;
	vec4 color = gl_Color;
	
	if (water){
		float x = vertex.x+xOffset;
		float z = vertex.z+zOffset;
		//vertex.y += cos(time/800.0+vertex.x*3.14155*factor+vertex.z*3.14155*factor) * 5;
		// Small waves
		vertex.y += cos(time/smallWaveCosTimeDiv + x*smallWaveCosFactorX + z*smallWaveCosFactorZ) * smallWaveCosGain;
		vertex.y += sin(time/smallWaveSinTimeDiv + x*smallWaveSinFactorX + z*smallWaveSinFactorZ) * smallWaveSinGain;
		// Big waves
		vertex.y += cos(time/bigWaveCosTimeDiv + x*bigWaveCosFactorX + z*bigWaveCosFactorZ) * bigWaveCosGain;
		vertex.y += sin(time/bigWaveSinTimeDiv + x*bigWaveSinFactorX + z*bigWaveSinFactorZ) * bigWaveSinGain;
		
		// Color depends on "derivee"
		float maxGain = smallWaveCosGain+smallWaveSinGain+bigWaveCosGain+bigWaveSinGain;
		colorLight = 1.0;
		colorLight += -1.0 * sin(time/smallWaveCosTimeDiv + x*smallWaveCosFactorX + z*smallWaveCosFactorZ) * smallWaveCosGain / maxGain;
		colorLight += cos(time/smallWaveSinTimeDiv + x*smallWaveSinFactorX + z*smallWaveSinFactorZ) * smallWaveSinGain / maxGain;
		colorLight += -1.0 * sin(time/bigWaveCosTimeDiv + x*bigWaveCosFactorX + z*bigWaveCosFactorZ) * bigWaveCosGain / maxGain;
		colorLight += cos(time/bigWaveSinTimeDiv + x*bigWaveSinFactorX + z*bigWaveSinFactorZ) * bigWaveSinGain / maxGain;
		
		
		colorLight = clamp(colorLight, 0.4, 1.0);
		colorLight *= lightFactor;
	}
	
	// Only do d computation
	vec3 vVertex = vec3(gl_ModelViewMatrix * vertex);
	d = length(/*gl_LightSource[0].position.xyz - */vVertex);
	
	
	gl_Position = gl_ModelViewProjectionMatrix * vertex;
	gl_FrontColor = gl_Color;
	gl_TexCoord[0] = gl_MultiTexCoord0;
}