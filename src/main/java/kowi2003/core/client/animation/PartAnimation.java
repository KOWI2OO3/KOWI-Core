package kowi2003.core.client.animation;

import java.util.List;

import javax.annotation.Nullable;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class PartAnimation extends AbstractAnimation {
    
    private List<KeyFrame> positionKeyFrames;
    private List<KeyFrame> rotationKeyFrames;
    private List<KeyFrame> scaleKeyFrames;

    private InterpolationState positionState;
    private InterpolationState rotationState;
    private InterpolationState scaleState;

    public PartAnimation(List<KeyFrame> positionKeyFrames, List<KeyFrame> rotationKeyFrames, List<KeyFrame> scaleKeyFrames) {
        this.positionKeyFrames = positionKeyFrames;
        this.rotationKeyFrames = rotationKeyFrames;
        this.scaleKeyFrames = scaleKeyFrames;

        positionState = initializeState(positionKeyFrames);
        rotationState = initializeState(rotationKeyFrames);
        scaleState = initializeState(scaleKeyFrames);
    }

    @Override
    public void increaseTime(float deltaTime) {
        if(isPaused) return;

        updateAnimation(deltaTime);

        if(time >= getMaxTime())
            reset();
    }

    public void updateAnimation(float deltaTime) {
        this.time += deltaTime;
        updateState(positionState, positionKeyFrames);
        updateState(rotationState, rotationKeyFrames);
        updateState(scaleState, scaleKeyFrames);
    }

    public void setTime(float time) {
        this.time = time;
        while(updateState(positionState, positionKeyFrames));
        while(updateState(rotationState, rotationKeyFrames));
        while(updateState(scaleState, scaleKeyFrames));
    }

    public Matrix4f getTransform() {
        Matrix4f transform = new Matrix4f();
        transform.translate(getValue(positionState, time, new Vector3f(0, 0, 0)));
        transform.rotateXYZ(getValue(rotationState, time, new Vector3f(0, 0, 0)));
        transform.scale(getValue(scaleState, time, new Vector3f(1, 1, 1)));
        return transform;
    }

    private boolean updateState(InterpolationState state, List<KeyFrame> frames) {
        if(state != null && state.isOutOfBounds(time)) 
        {
            state.from = state.to;
            state.lastIndex++;
            if(frames.size() > state.lastIndex) {
                state.to = frames.get(state.lastIndex);
                return true;
            }
        }
        return false;
    }

    public float maxTime() {
        var positionMax = positionKeyFrames != null && positionKeyFrames.size() > 0 ? positionKeyFrames.get(positionKeyFrames.size() - 1).time() : -1f;
        var rotationMax = rotationKeyFrames != null && rotationKeyFrames.size() > 0 ? rotationKeyFrames.get(rotationKeyFrames.size() - 1).time() : -1f;
        var scaleMax = scaleKeyFrames != null && scaleKeyFrames.size() > 0 ? scaleKeyFrames.get(scaleKeyFrames.size() - 1).time() : -1f;

        return Math.max(positionMax, Math.max(scaleMax, rotationMax));
    }

    protected void resetAnimation() {
        resetState(positionState, positionKeyFrames);
        resetState(rotationState, rotationKeyFrames);
        resetState(scaleState, scaleKeyFrames);
    }

    private void resetState(InterpolationState state, List<KeyFrame> frames) {
        if(frames != null && state != null) {
            state.from = frames.get(0);
            state.to = frames.get(1);
            state.lastIndex = 1;
        }
    }

    @Nullable
    private static InterpolationState initializeState(List<KeyFrame> frames) {
        return frames != null && frames.size() > 0 ? new InterpolationState(frames.get(0), frames.get(1), 1) : null;
    }

    private Vector3f getValue(InterpolationState state, float time, Vector3f fallback) {
        return state != null ? state.getValue(time) : fallback; 
    }

}
