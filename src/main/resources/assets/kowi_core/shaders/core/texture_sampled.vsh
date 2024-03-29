#version 150

#moj_import <light.glsl>
#moj_import <fog.glsl>

in vec3 Position;
in vec4 Color;
in vec2 UV0;
in ivec2 UV2;
in vec3 Normal;

uniform sampler2D Sampler0;
uniform sampler2D Sampler2;
uniform sampler2D Sampler3;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform vec3 ChunkOffset;
uniform int FogShape;

out float vertexDistance;
out vec4 vertexColor;
out vec2 texCoord0;
out vec4 normal;

vec4 averageColor(float steps) {
    vec4 colors = vec4(0, 0, 0, 1);
    int count = 0;
    for(float i = 0; i < 1; i += 1/steps) {
        for(float j = 0; j < 1; j += 1/steps) {
            vec2 uv = vec2(i, j);
            vec4 color = texture(Sampler3, uv);
            if(color.a > 0.5) {
                colors += color;
                count++;
            }
        }
    }

    return vec4((colors / (steps * steps)).xyz, 1.0);
}

void main() {
    vec3 pos = Position + ChunkOffset;
    gl_Position = ProjMat * ModelViewMat * vec4(pos, 1.0);

    vertexDistance = fog_distance(ModelViewMat, pos, FogShape);
    vertexColor = Color * minecraft_sample_lightmap(Sampler2, UV2) * averageColor(64);
    texCoord0 = UV0;
    normal = ProjMat * ModelViewMat * vec4(Normal, 0.0);
}
