#version 150

float rand(float n){return fract(sin(n) * 43758.5453123);}

float noise(float p){
    float fl = floor(p);
    float fc = fract(p);
	return mix(rand(fl), rand(fl + 1.0), fc);
}

float mod289(float x){return x - floor(x / 289.0) * 289.0;}
vec3 mod289(vec3 x){return x - floor(x / 289.0) * 289.0;}
vec4 mod289(vec4 x){return x - floor(x / 289.0) * 289.0;}
vec4 perm(vec4 x){return mod289(((x * 34.0) + 1.0) * x);}

vec4 taylorInvSquare(vec4 r) {
    return 1.79284291400159 - r * 0.85373472095314;
}

float snoise(vec3 samplePoint) {
    vec3 v = samplePoint;
    const vec2 C = vec2(1.0 / 6.0, 1.0 / 3.0);

    // First corner
    vec3 i = floor(v + dot(v, C.yyy));
    vec3 x0 = v - i + dot(i, C.xxx);

    // Other corners
    vec3 g = step(x0.yzx, x0.xyz);
    vec3 l = 1.0 - g;
    vec3 i1 = min(g.xyz, l.zxy);
    vec3 i2 = max(g.xyz, l.zxy);

    vec3 x1 = x0 - i1 + C.xxx;
    vec3 x2 = x0 - i2 + C.yyy;
    vec3 x3 = x0 - 0.5;

    // Permutations
    i = mod289(i);  // Avoid truncation effects in permutation
    vec4 p = 
        perm(perm(perm(i.z + vec4(0.0, i1.z, i2.z, 1.0))
                        + i.y + vec4(0.0, i1.y, i2.y, 1.0))
                        + i.x + vec4(0.0, i1.x, i2.x, 1.0));

    // Gradients: 7x7 points over a square, mapped onto an octahedron
    // The ring size 17*17 = 289 is close to a multiple of 49 (49*6 = 294)
    vec4 j = p - 49.0 * floor(p / 49.0); // mod(p, 7*7)

    vec4 x_ = floor(j / 7.0);
    vec4 y_ = floor(j - 7.0 * x_);  // mod(j, N)
    vec4 x = (x_ * 2.0 + 0.5) / 7.0 - 1.0;
    vec4 y = (y_ * 2.0 + 0.5) / 7.0 - 1.0;

    vec4 h = 1.0 - abs(x) - abs(y);

    vec4 b0 = vec4(x.xy, y.xy);
    vec4 b1 = vec4(x.zw, y.zw);

    vec4 s0 = floor(b0) * 2.0 + 1.0;
    vec4 s1 = floor(b1) * 2.0 + 1.0;
    vec4 sh = -step(h, vec4(0.0));

    vec4 a0 = b0.xzyw + s0.xzyw * sh.xxyy;
    vec4 a1 = b1.xzyw + s1.xzyw * sh.zzww;

    vec3 g0 = vec3(a0.xy, h.x);
    vec3 g1 = vec3(a0.zw, h.y);
    vec3 g2 = vec3(a1.xy, h.z);
    vec3 g3 = vec3(a1.zw, h.w);

    // Normalise gradients
    vec4 norm = taylorInvSquare(vec4(dot(g0, g0), dot(g1, g1), dot(g2, g2), dot(g3, g3)));
    g0 *= norm.x;
    g1 *= norm.y;
    g2 *= norm.z;
    g3 *= norm.w;

    // Mix final noise value
    vec4 m = max(0.6 - vec4(dot(x0, x0), dot(x1, x1), dot(x2, x2), dot(x3, x3)), 0.0);
    m = m * m;
    m = m * m;

    vec4 px = vec4(dot(x0, g0), dot(x1, g1), dot(x2, g2), dot(x3, g3));
    return 42.0 * dot(m, px);
}

float noise(vec3 p){
    vec3 a = floor(p);
    vec3 d = p - a;
    d = d * d * (3.0 - 2.0 * d);

    vec4 b = a.xxyy + vec4(0.0, 1.0, 0.0, 1.0);
    vec4 k1 = perm(b.xyxy);
    vec4 k2 = perm(k1.xyxy + b.zzww);

    vec4 c = k2 + a.zzzz;
    vec4 k3 = perm(c);
    vec4 k4 = perm(c + 1.0);

    vec4 o1 = fract(k3 * (1.0 / 41.0));
    vec4 o2 = fract(k4 * (1.0 / 41.0));

    vec4 o3 = o2 * d.z + o1 * (1.0 - d.z);
    vec2 o4 = o3.yw * d.x + o3.xz * (1.0 - d.x);

    return o4.y * d.y + o4.x * (1.0 - d.y);
}

struct noiseParams {
    int numLayers;
    float lacunarity;
    float persistance;
    float power;
    float multiplier;
    float grain;
    float verticalShift;
    vec3 offset;
    float scale;
};

noiseParams constructParams() {
    noiseParams params;
    params.numLayers = 5;
    params.lacunarity = 4; // 2
    params.persistance = .5;
    params.power = 1;
    params.multiplier = 1;
    params.grain = 1;
    params.offset = vec3(0, 0, 0);
    params.scale = 1;
    params.verticalShift = 0;
    return params;
}

noiseParams copyParams(noiseParams toCopy) {
    noiseParams params;
    params.numLayers = toCopy.numLayers;
    params.lacunarity = toCopy.lacunarity;
    params.persistance = toCopy.persistance;
    params.power = toCopy.power;
    params.grain = toCopy.grain;
    params.multiplier = toCopy.multiplier;
    params.offset = toCopy.offset;
    params.scale = toCopy.scale;
    params.verticalShift = toCopy.verticalShift;
    return params;
}

float fractalNoise(vec3 samplePoint, noiseParams params) {
    float noiseSum = 0;
    float amplitude = 1;
    float frequency = params.scale;

    for(int i = 0; i < params.numLayers; i++) {
        // Sample noise function and add to the result
        noiseSum += snoise(samplePoint * frequency + params.offset) * amplitude;
        // Make each layer more and more detailed
        frequency *= params.lacunarity;
        // Make each layer contribute less and less to result
        amplitude *= params.persistance;
    }
    return noiseSum * params.multiplier + params.verticalShift;
}  

float fractalNoise(vec3 point, float numIterations) {
    float noiseSum = 0;
    float amplitude = 1;
    float frequency = 1;

    for(int i = 0; i < numIterations; i++) {
        // Sample noise function and add to the result
        noiseSum += snoise(point * frequency) * amplitude;
        // Make each layer more and more detailed
        frequency *= 2;
        // Make each layer contribute less and less to result
        amplitude *= .5;
    }
    return noiseSum;
}  

float fractalNoise(vec3 point) {
    float noiseSum = 0;
    float amplitude = 1;
    float frequency = 1;

    for(int i = 0; i < 5; i++) {
        // Sample noise function and add to the result
        noiseSum += snoise(point * frequency) * amplitude;
        // Make each layer more and more detailed
        frequency *= 2;
        // Make each layer contribute less and less to result
        amplitude *= .5;
    }
    return noiseSum;
}

float simpleNoise(vec3 samplePoint, noiseParams params) {
    // Sum up noise layers
    float noiseSum = 0;
    float amplitude = 1;
    float frequency = params.scale;

    for(int i = 0; i < params.numLayers; i++) {
        noiseSum += snoise(samplePoint * frequency + params.offset) * amplitude;

        amplitude *= params.persistance;
        frequency *= params.lacunarity;
    }
    return noiseSum * params.multiplier + params.verticalShift;
}

float rigidNoise(vec3 samplePoint, noiseParams params) {

    float noiseSum = 0;
    float amplitude = 1;
    float frequency = params.scale;
    float rigdeWeight = 1; 

    for(int i = 0; i < params.numLayers; i++) {
        float noiseVal = 1 - abs(snoise(samplePoint * frequency + params.offset));
        noiseVal = pow(abs(noiseVal), params.power);
        noiseVal *= rigdeWeight;
        rigdeWeight = clamp(noiseVal * params.grain, 0, 1);

        noiseSum += noiseVal * amplitude;
        amplitude *= params.persistance;
        frequency *= params.lacunarity;
    }

    return noiseSum * params.multiplier + params.verticalShift;
}

float smoothedRidgidNoise(vec3 samplePoint, noiseParams params) {
    vec3 sphereNormal = normalize(samplePoint);
    vec3 axisA = cross(sphereNormal, vec3(0, 1, 0));
    vec3 axisB = cross(sphereNormal, axisA);

    float peakSmoothing = 1;

    float offsetDst = peakSmoothing * 0.01;
    float sample0 = rigidNoise(samplePoint, params);
    float sample1 = rigidNoise(samplePoint - axisA * offsetDst, params);
    float sample2 = rigidNoise(samplePoint + axisA * offsetDst, params);
    float sample3 = rigidNoise(samplePoint - axisB * offsetDst, params);
    float sample4 = rigidNoise(samplePoint + axisB * offsetDst, params);
    return (sample0, sample1, sample2, sample3, sample4) / 5;
}