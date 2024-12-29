package kowi2003.core.common.helpers;

import org.joml.Vector3f;

import net.minecraft.world.phys.Vec3;

public final class MathHelper {
    
    public static Vec3 lerp(Vec3 a, Vec3 b, float t)  {
        return a.add(b.subtract(a).multiply(t, t, t));
    }

    public static Vector3f lerp(Vector3f a, Vector3f b, float t)  {
        Vector3f ca = new Vector3f(a);
        Vector3f cb = new Vector3f(b);
        return ca.add(cb.sub(ca).mul(t));
    }

    public static float inverseLerp(float a, float b, float lerped) {
        float min = Math.min(a, b);
        float max = Math.max(a, b); 
        return (lerped - min) / (max - min);
    }

    public static float clamp(float t, float min, float max) {
        return Math.min(Math.max(t, min), max);
    }
}
