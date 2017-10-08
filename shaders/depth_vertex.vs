#version 300 es

const int MAX_WEIGHTS = 4;
const int MAX_JOINTS = 150;

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 texCoord;
layout (location = 2) in vec3 vertexNormal;
layout (location = 3) in vec4 jointWeights;
layout (location = 4) in ivec4 jointIndices;
layout (location = 5) in mat4 modelViewInstancedMatrix;
layout (location = 9) in mat4 modelLightViewInstancedMatrix;

uniform int isInstanced;
uniform mat4 modelLightViewNonInstancedMatrix;
uniform mat4 jointsMatrix[MAX_JOINTS];
uniform mat4 orthoProjectionMatrix;

void main()
{
    vec4 initPos = vec4(0, 0, 0, 0);
    mat4 modelLightViewMatrix;
    if ( isInstanced > 0 )
    {
        modelLightViewMatrix = modelLightViewInstancedMatrix;
        initPos = vec4(position, 1.0);
    }
    else
    {
        modelLightViewMatrix = modelLightViewNonInstancedMatrix;

        int count = 0;
        for(int i = 0; i < MAX_WEIGHTS; i++)
        {
            float weight = jointWeights[i];
            if(weight > 0.0) {
                count++;
                int jointIndex = jointIndices[i];
                vec4 tmpPos = jointsMatrix[jointIndex] * vec4(position, 1.0);
                initPos += weight * tmpPos;
            }
        }
        if (count == 0)
        {
            initPos = vec4(position, 1.0);
        }
    }
    gl_Position = orthoProjectionMatrix * modelLightViewMatrix * initPos;
}