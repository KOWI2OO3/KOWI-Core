package kowi2003.core.client.model;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.client.model.renderable.ITextureRenderTypeLookup;

public interface IModel {
    
    public void render(PoseStack pose, MultiBufferSource buffersrc, ITextureRenderTypeLookup rendertypeLookup, int combinedLightIn, int combinedOverlayIn, float partialTicks);

    default void render(PoseStack pose, MultiBufferSource buffersrc, RenderType renderType, int combinedLightIn, int combinedOverlayIn) 
    {
        render(pose, buffersrc, (location) -> renderType, combinedLightIn, combinedOverlayIn, 0);
    }

}
