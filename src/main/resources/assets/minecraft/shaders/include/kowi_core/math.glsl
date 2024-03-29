#version 150

float lerp(float a, float b, float k) {
    return a + (b-a) * k;
}

vec2 lerp(vec2 a, vec2 b, float k) {
    return a + (b-a) * k;
}

vec3 lerp(vec3 a, vec3 b, float k) {
    return k >= 1 ? b : a + (b-a) * k;
}

vec4 lerp(vec4 a, vec4 b, float k) {
    return a + (b-a) * k;
}

vec2 sanitize(vec2 point) {
    float maxValue = max(point.x, point.y);
    return point / maxValue;
}

vec3 sanitize(vec3 point) {
    float maxValue = max(point.x, max(point.y, point.z));
    return point / maxValue;
}

vec4 sanitize(vec4 point) {
    float maxValue = max(max(point.x, point.y), max(point.z, point.w));
    return point / maxValue;
}

float smoothMin(float a, float b, float k) {
    float h = clamp(0.5 + 0.5 * (b - a)/k, 0.0, 1.0);
    return mix(b, a, h) - k * h * (1.0-h);
}

vec2 smoothMin(vec2 a, vec2 b, float k) {
    vec2 h = clamp(0.5 + 0.5 * (b - a)/k, 0.0, 1.0);
    return mix(b, a, h) - k * h * (1.0-h);
}

vec3 smoothMin(vec3 a, vec3 b, float k) {
    vec3 h = clamp(0.5 + 0.5 * (b - a)/k, 0.0, 1.0);
    return mix(b, a, h) - k * h * (1.0-h);
}

float smoothMax(float a, float b, float k) {
    return smoothMin(a, b, -k);
}

vec2 smoothMax(vec2 a, vec2 b, float k) {
    return smoothMin(a, b, -k);
}

vec3 smoothMax(vec3 a, vec3 b, float k) {
    return smoothMin(a, b, -k);
}
