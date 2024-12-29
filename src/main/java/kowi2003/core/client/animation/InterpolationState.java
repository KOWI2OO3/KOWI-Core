package kowi2003.core.client.animation;

import org.joml.Vector3f;

import kowi2003.core.common.helpers.MathHelper;

public class InterpolationState {
    
    int lastIndex;

    KeyFrame from;
    KeyFrame to;

    public InterpolationState(KeyFrame from, KeyFrame to, int lastIndex) {
        this.lastIndex = lastIndex;
        this.from = from;
        this.to = to;
    }

    public Vector3f getValue(float time) {
        return MathHelper.lerp(from.value(), to.value(), MathHelper.clamp(MathHelper.inverseLerp(from.time(), to.time(), time), 0, 1));
    }

    public boolean isOutOfBounds(float time) { return time > to.time(); }
}
