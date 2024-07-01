package kowi2003.core.utils;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;

/**
 * Context for rendering
 * 
 * @author KOWI2003
 */
public record RenderContext(PoseStack pose, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
   
    public RenderContext(PoseStack pose, MultiBufferSource bufferSource) {
        this(pose, bufferSource, RenderHelper.defaultCombinedLight, RenderHelper.defaultCombinedOverlay);
    }

}
