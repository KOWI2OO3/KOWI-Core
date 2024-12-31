package kowi2003.core.client.animation;

import javax.annotation.Nonnull;

import kowi2003.core.client.model.IAnimatedModel;
import kowi2003.core.client.model.IModel;
import net.minecraftforge.client.model.renderable.CompositeRenderable.Transforms;

public class Animator {
    
    @Nonnull
    private IAnimationType type;
    private Animation animation;

    public Animator(IAnimationType type) {
        this.type = type;
    }

    public void play() {
        animation = type.getAnimation();
    }

    public void pause() {
        if(animation != null)
            animation.pause();
    }

    public boolean isPaused() {
        return animation != null ? animation.isPaused : true; 
    }

    public void stop() {
        if(animation != null) 
        {
            animation.stop();
            animation = null;
        }
    }

    /**
     * Increments the time of the animation if it is playing
     * @param deltaTime the time difference between the last time calling the function, usually use partialTicks / 40
     */
    public void increaseTime(float deltaTime) {
        if(animation == null) return;

        animation.increaseTime(deltaTime);

        if(animation.isStopped)
            animation = null;
    }

    /**
     * gets the transforms used to animate a model
     * @return the transforms object used to animate the model
     */
    public Transforms geTransforms() {
        return animation != null ? animation.getTransforms() : Transforms.EMPTY;
    }

    /**
     * Applies the animation to the model, if the model is an animated model and the animation is not stopped
     * @param model the model to apply the animation on
     * @return the transforms used to animate the model, returned for efficiency reasons to prevent multiple calculations of the transforms 
     * when needed for multiple models or seperate rendered parts
     */
    public Transforms applyAnimation(IModel model) {
        var transforms = geTransforms();
        if(model instanceof IAnimatedModel animatedModel && animation != null)
            animatedModel.applyAnimation(transforms);
        return transforms;
    }
}
