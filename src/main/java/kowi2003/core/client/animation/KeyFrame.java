package kowi2003.core.client.animation;

import java.util.Optional;

import javax.annotation.Nullable;

import org.joml.Vector3f;

public record KeyFrame(float time, @Nullable Vector3f value) {

    public boolean hasValue() { return value != null; }

    public Optional<Vector3f> getValue() {
        return hasValue() ? Optional.of(value()) : Optional.empty();
    }

}
