package kowi2003.core.client.model;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.model.renderable.ITextureRenderTypeLookup;

public record JsonModel(BakedModel model) implements IModel {

    @Override
    @SuppressWarnings("null")
    public void render(PoseStack pose, MultiBufferSource buffersrc, ITextureRenderTypeLookup rendertypeLookup,
            int combinedLightIn, int combinedOverlayIn, float partialTicks) {
        var hasRenderType = rendertypeLookup != null && rendertypeLookup.get(new ResourceLocation("")) != null;
        var random = RandomSource.create();
        var renderer = Minecraft.getInstance().getItemRenderer();

        var fabulous = true;

        for(var renderPass : model.getRenderPasses(ItemStack.EMPTY, fabulous)) 
        {
            var rendertypes = renderPass.getRenderTypes(ItemStack.EMPTY, fabulous);
            var rendertype = !hasRenderType ? rendertypes.get(0) : rendertypeLookup.get(new ResourceLocation(""));
            var consumer = ItemRenderer.getFoilBufferDirect(buffersrc, rendertype, false, false);

            renderer.renderQuadList(pose, consumer, renderPass.getQuads(null, null, random, null, rendertype), ItemStack.EMPTY, combinedLightIn, combinedOverlayIn);
        }
    }
    
    
}
