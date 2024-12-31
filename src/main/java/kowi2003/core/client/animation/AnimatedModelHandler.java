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
import net.minecraftforge.client.model.renderable.ITextureRenderTypeLookup;
import net.minecraftforge.client.model.renderable.CompositeRenderable.Transforms;
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
    
    public static Transforms applyAnimation(@Nonnull BlockEntity tile, @Nonnull IAnimationType type, IModel model) {
        return applyAnimation(uuidFromBlockEntity(tile), type, model);
    }

    public static Transforms applyAnimation(@Nonnull UUID entityId, @Nonnull IAnimationType type, IModel model) {
        var animator = getAnimator(entityId, type);
        return animator != null ? animator.applyAnimation(model) : Transforms.EMPTY;
    }

    public static Transforms renderAnimatedModel(@Nonnull BlockEntity tile, IAnimationType type, IModel model, PoseStack pose, MultiBufferSource bufferSource, 
            ITextureRenderTypeLookup rendertypeLookup, int combinedLightIn, int combinedOverlayIn, float partialTicks) {
        return renderAnimatedModel(uuidFromBlockEntity(tile), type, model, pose, bufferSource, rendertypeLookup, combinedLightIn, combinedOverlayIn, partialTicks);
    }

    public static Transforms renderAnimatedModel(@Nonnull UUID entityId, IAnimationType type, IModel model, PoseStack pose, MultiBufferSource bufferSource, 
            ITextureRenderTypeLookup rendertypeLookup, int combinedLightIn, int combinedOverlayIn, float partialTicks) {
        var transforms = applyAnimation(entityId, type, model);

        if(model != null)
            model.render(pose, bufferSource, rendertypeLookup, combinedLightIn, combinedOverlayIn, partialTicks);

        return transforms;
    }

    @Nullable
    public static Animator getAnimator(@Nonnull BlockEntity tile, @Nonnull IAnimationType type) {
        return getAnimator(uuidFromBlockEntity(tile), type);
    }

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
            for (var value : cache.values()) {
                for (var animator : value.values()) {
                    animator.increaseTime(event.renderTickTime / 40f);
                }
            }
        }
    }

}
