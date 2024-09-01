package kowi2003.core.client.model;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraftforge.client.model.renderable.CompositeRenderable;
import net.minecraftforge.client.model.renderable.CompositeRenderable.Transforms;
import net.minecraftforge.client.model.renderable.ITextureRenderTypeLookup;

public final class WavefontModel implements IModel 
{
    private final CompositeRenderable renderable;
    private Transforms transforms;

    public WavefontModel(CompositeRenderable renderable) {
        this.renderable = renderable;
        this.transforms = Transforms.EMPTY;
    }

    @Override
    public void render(PoseStack pose, MultiBufferSource buffersrc, ITextureRenderTypeLookup rendertypeLookup,
            int combinedLightIn, int combinedOverlayIn, float partialTicks) {
        renderable.render(pose, buffersrc, rendertypeLookup, combinedLightIn, combinedOverlayIn, partialTicks, transforms);
    }
    
}
