#version 110

uniform vec3 playerPos;
uniform bool drawMap;
uniform float echoDistance,echoPower;

void main()
{
	float distanceFromPlayer,echoFactor;
	vec4 position,color;
	
	position = ftransform();
	color = gl_Color;
	
	if (drawMap){
		distanceFromPlayer = distance((gl_ModelViewProjectionMatrix * vec4(playerPos,1)).xyz, position.xyz)/100.0;
		if (distanceFromPlayer<echoDistance){
			// The closer to echo line it is, the brighter it will be
			echoFactor = echoPower / max((echoDistance-distanceFromPlayer),1.0);
			echoFactor = clamp(echoFactor,1.0,3.0);
			color.a = color.a * echoFactor;
		}
	}
	
	
	gl_Position = position;
	gl_FrontColor = color;
}