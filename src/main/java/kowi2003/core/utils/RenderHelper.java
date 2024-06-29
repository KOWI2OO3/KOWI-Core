package kowi2003.core.utils;

import javax.annotation.Nonnull;

import org.joml.Vector3f;

import com.mojang.blaze3d.vertex.PoseStack;

import kowi2003.core.contraptions.BlockData;
import kowi2003.core.contraptions.Contraption;
import kowi2003.core.contraptions.ContraptionWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;

/**
 * Helper class for rendering contraptions
 * 
 * @author KOWI2003
 */
public class RenderHelper {
    
    /**
     * Renders the given blocks at the given position
     * @param pose the pose stack to render with
     * @param buffersrc the buffer source to render with
     * @param blocks the blocks to render
     * @param position the position to render the blocks at
     */
    @SuppressWarnings("null")
    public static void renderBlocks(PoseStack pose, MultiBufferSource buffersrc, BlockData[] blocks, Vector3f position) {
        Minecraft mc = Minecraft.getInstance();
        if(mc.level == null) return;
        
        var wrapper = new ContraptionWrapper(new Contraption(blocks), mc.level);
        wrapper.setPosition(position);
        renderContraption(pose, buffersrc, wrapper);
    }

    /**
     * Renders the given contraption at the given position
     * @param pose the pose stack to render with
     * @param buffersrc the buffer source to render with
     * @param wrapper the contraption wrapper to render
     */
    @SuppressWarnings("null")
    public static void renderContraption(PoseStack pose, MultiBufferSource buffersrc, @Nonnull ContraptionWrapper wrapper) {
        var mc = Minecraft.getInstance();
        var position = wrapper.position();

        pose.pushPose();
        pose.translate(position.x, position.y, position.z);
        pose.rotateAround(wrapper.rotation(), 0.5f, 0, 0.5f);

        for (BlockPos blockPosition : wrapper.contraption()) {
            var state = wrapper.contraption().getState(blockPosition);

            pose.pushPose();
            pose.translate(blockPosition.getX(), blockPosition.getY(), blockPosition.getZ());
            var renderType = RenderType.cutoutMipped();
            mc.getBlockRenderer().renderBatched(state, blockPosition, wrapper, pose, buffersrc.getBuffer(renderType), true, RandomSource.create(), null, renderType);
            pose.popPose();
        }
        pose.popPose();
    }

    /**
     * Renders the highlight shape of the block at the given position
     * @param pose the pose stack to render with
     * @param buffersrc the buffer source to render with
     * @param wrapper the contraption wrapper to render
     * @param highlightPosition the position of the block to render the highlight of (in contraption space)
     */
    public static void renderContraptionHighlight(PoseStack pose, MultiBufferSource buffersrc, @Nonnull ContraptionWrapper wrapper, @Nonnull BlockPos highlightPosition) {
        var position = wrapper.position();
        var rotation = wrapper.rotation();
        
        var state = wrapper.contraption().getState(highlightPosition);
        
        pose.pushPose();
        pose.translate(position.x, position.y, position.z);
        pose.rotateAround(rotation, 0.5f, 0, 0.5f);
        pose.translate(highlightPosition.getX(), highlightPosition.getY(), highlightPosition.getZ());

        var shape = state.getCollisionShape(wrapper, highlightPosition);

        var buffer = buffersrc.getBuffer(RenderType.lines());
        LevelRenderer.renderVoxelShape(pose, buffer, shape, 0, 0, 0, 0f, 0f, 0f, 0.5f, false);

        pose.popPose();
    }

}
