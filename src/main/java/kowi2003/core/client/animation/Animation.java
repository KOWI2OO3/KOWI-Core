package kowi2003.core.client.animation;

import java.util.HashMap;

import org.joml.Matrix4f;

import com.google.common.collect.ImmutableMap;

import net.minecraftforge.client.model.renderable.CompositeRenderable.Transforms;

public class Animation extends AbstractAnimation {
    
    private HashMap<String, PartAnimation> partAnimations = new HashMap<>();

    public Animation(HashMap<String, PartAnimation> partAnimations) {
        this.partAnimations = partAnimations;
    }

    @Override
    public void updateAnimation(float deltaTime) {
        partAnimations.forEach((name, animation) -> animation.updateAnimation(deltaTime));
    }

    public Transforms getTransforms() {
        var map = new HashMap<String, Matrix4f>();
        for (var entry : partAnimations.entrySet())
            map.put(entry.getKey(), entry.getValue().getTransform());
            
        return Transforms.of(ImmutableMap.copyOf(map));
    }
    
    @Override
    public void resetAnimation() {
        partAnimations.values().forEach(PartAnimation::reset);
    }

    @Override
    public float maxTime() {
        var max = -1f;
        for (var animation : partAnimations.values()) {
            max = Math.max(animation.maxTime(), maxTime);
        }
        return max;
    }

    public Animation copy(Animation animation) {
        if(animation == null)
            return new Animation(partAnimations);

        animation.time = 0;
        animation.maxTime = -1;
        animation.partAnimations = animation.partAnimations;
        return animation;
    }
}
