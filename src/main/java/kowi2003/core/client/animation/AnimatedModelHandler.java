package kowi2003.core.client.animation;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;

import kowi2003.core.Core;
import kowi2003.core.client.model.IModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.client.model.renderable.CompositeRenderable.Transforms;
import net.minecraftforge.client.model.renderable.ITextureRenderTypeLookup;
import net.minecraftforge.event.TickEvent.RenderTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

public final class AnimatedModelHandler {
    
    private static Map<UUID, HashMap<IAnimationType, Animator>> cache = new HashMap<>();

    public static Animator playAnimation(@Nonnull BlockEntity tile, @Nonnull IAnimationType type) {
        return playAnimation(uuidFromBlockEntity(tile), type);
    }

    public static Animator playAnimation(@Nonnull UUID entityId, @Nonnull IAnimationType type) {
        var currentMap = cache.get(entityId);
        if(currentMap == null)
            currentMap = new HashMap<>();

        var animator = currentMap.put(type, new Animator(type));
        cache.put(entityId, currentMap);

        return animator;
    }
    
    /**
     * Applies the animation to the model, if the model is an animated model and the animation is not stopped
     * @param tile the blockentity reference to use to find the animator
     * @param type the animation type used to find the reference
     * @param model the model to apply the animation on
     * @return the transforms used to animate the model, returned for efficiency reasons to prevent multiple calculations of the transforms 
     * when needed for multiple models or seperate rendered parts
     */
    public static Transforms applyAnimation(@Nonnull BlockEntity tile, @Nonnull IAnimationType type, IModel model) {
        return applyAnimation(uuidFromBlockEntity(tile), type, model);
    }
    
    /**
     * Applies the animation to the model, if the model is an animated model and the animation is not stopped
     * @param entityId the entity id used to find the animator
     * @param type the animation type used to find the reference
     * @param model the model to apply the animation on
     * @return the transforms used to animate the model, returned for efficiency reasons to prevent multiple calculations of the transforms 
     * when needed for multiple models or seperate rendered parts
     */
    public static Transforms applyAnimation(@Nonnull UUID entityId, @Nonnull IAnimationType type, IModel model) {
        var animator = getAnimator(entityId, type);
        return animator != null ? animator.applyAnimation(model) : Transforms.EMPTY;
    }

    /**
     * Applies the animation to the model, if the model is an animated model and the animation is not stopped and renders the model
     * @param tile the blockentity reference to use to find the animator
     * @param type the animation type used to find the reference
     * @param model the model to apply the animation to and render
     * @return the transforms used to animate the model, returned for efficiency reasons to prevent multiple calculations of the transforms 
     * when needed for multiple models or seperate rendered parts
     */
    public static Transforms renderAnimatedModel(@Nonnull BlockEntity tile, IAnimationType type, IModel model, PoseStack pose, MultiBufferSource bufferSource, 
            ITextureRenderTypeLookup rendertypeLookup, int combinedLightIn, int combinedOverlayIn, float partialTicks) {
        return renderAnimatedModel(uuidFromBlockEntity(tile), type, model, pose, bufferSource, rendertypeLookup, combinedLightIn, combinedOverlayIn, partialTicks);
    }

    /**
     * Applies the animation to the model, if the model is an animated model and the animation is not stopped and renders the model
     * @param entityId the entity id used to find the animator
     * @param type the animation type used to find the reference
     * @param model the model to apply the animation to and render
     * @return the transforms used to animate the model, returned for efficiency reasons to prevent multiple calculations of the transforms 
     * when needed for multiple models or seperate rendered parts
     */
    public static Transforms renderAnimatedModel(@Nonnull UUID entityId, IAnimationType type, IModel model, PoseStack pose, MultiBufferSource bufferSource, 
            ITextureRenderTypeLookup rendertypeLookup, int combinedLightIn, int combinedOverlayIn, float partialTicks) {
        var transforms = applyAnimation(entityId, type, model);

        if(model != null)
            model.render(pose, bufferSource, rendertypeLookup, combinedLightIn, combinedOverlayIn, partialTicks);

        return transforms;
    }

    /**
     * gets the animator stored based on the block entity and the animation type
     * @param tile the blockentity reference to use to find the animator
     * @param type the animation type used to find the reference
     * @return the animator if it exists in the designated place, or null if no animator was found
     */
    @Nullable
    public static Animator getAnimator(@Nonnull BlockEntity tile, @Nonnull IAnimationType type) {
        return getAnimator(uuidFromBlockEntity(tile), type);
    }

    /**
     * gets the animator stored based on the block entity and the animation type
     * @param entityId the entity id used to find the animator
     * @param type the animation type used to find the reference
     * @return the animator if it exists in the designated place, or null if no animator was found
     */
    @Nullable
    public static Animator getAnimator(@Nonnull UUID entityId, @Nonnull IAnimationType type) {
        var currentMap = cache.get(entityId);
        if(currentMap != null) {
            var animator = currentMap.get(type);
            if(animator != null)
                return animator;
        }
        return null;
    }
    
    private static UUID uuidFromBlockEntity(@Nonnull BlockEntity tile) {
        var level = tile.getLevel();
        long dimHash = 0;
        if(level != null)
            dimHash = level.dimension().hashCode() << (4 * 8);
        return new UUID(dimHash, tile.getBlockPos().asLong());
    }

    @Mod.EventBusSubscriber(bus = Bus.FORGE, modid = Core.MODID)
    public static final class AnimatorHandler {

        /**
         * Do not use this method it is used for internal use only!
         */
        @Deprecated
        @SubscribeEvent
        public static void onRenderTick(RenderTickEvent event) {
            // Tick all animators
            // Set<Pair<>> invalidAnimators;

            for (var entry : cache.entrySet()) {
                var value = entry.getValue();
                for (var referenceAnimatorEntry : value.entrySet()) {
                    var animator = referenceAnimatorEntry.getValue();
                    animator.updateAnimation(event.renderTickTime / 40f);
                    if(animator.isStopped()) {
                        // Save to be removed
                    }
                }
            }
        }
    }

}
