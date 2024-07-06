package kowi2003.core.utils;

import javax.annotation.Nonnull;

import org.joml.AxisAngle4d;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

import kowi2003.core.contraptions.BlockData;
import kowi2003.core.contraptions.Contraption;
import kowi2003.core.contraptions.ContraptionHelper;
import kowi2003.core.contraptions.ContraptionWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.model.data.ModelData;

/**
 * Helper class for rendering contraptions
 * 
 * @author KOWI2003
 */
public class RenderHelper {

    public static final int defaultCombinedLight = 15728880;
    public static final int defaultCombinedOverlay = OverlayTexture.NO_OVERLAY;
    
    public static void bindEmptyTexture() {
		bindTexture(getEmptyTexture());
	}
	
	public static ResourceLocation getEmptyTexture() {
		return new ResourceLocation("textures/misc/white.png");
	}

    public static void bindTexture(ResourceLocation textureLocation) {
		if(textureLocation != null)
			RenderSystem.setShaderTexture(0, textureLocation);
	}
	
	public static void bindLightTexture(ResourceLocation textureLocation) {
		if(textureLocation != null)
			RenderSystem.setShaderTexture(2, textureLocation);
	}
	
	public static void bindOverlayTexture(ResourceLocation textureLocation) {
		if(textureLocation != null)
			RenderSystem.setShaderTexture(1, textureLocation);
	}
	
    /**
     * Renders the given blocks at the given position
     * @param pose the pose stack to render with
     * @param buffersrc the buffer source to render with
     * @param blocks the blocks to render
     * @param position the position to render the blocks at
     */
    @SuppressWarnings("null")
    public static void renderBlocks(RenderContext context, BlockData[] blocks, Vector3f position) {
        Minecraft mc = Minecraft.getInstance();
        if(mc.level == null) return;
        
        var wrapper = new ContraptionWrapper(new Contraption(blocks), mc.level);
        wrapper.setPosition(new Vec3(position));
        renderContraption(context, wrapper);
    }

    /**
     * Renders the given contraption at the given position
     * @param pose the pose stack to render with
     * @param buffersrc the buffer source to render with
     * @param contraption the contraption to render
     */
    @SuppressWarnings("null")
    public static void renderContraption(RenderContext context, @Nonnull ContraptionWrapper wrapper) {
        var mc = Minecraft.getInstance();
        var position = wrapper.position();
        var pose = context.pose();
        
        var randomsource = RandomSource.create();

        pose.pushPose();
        pose.translate(position.x, position.y, position.z);
        pose.rotateAround(wrapper.rotation(), 0.5f, 0, 0.5f);

        wrapper = prepareWrapper(wrapper);
        for (BlockPos blockPosition : wrapper.contraption()) {
            var state = wrapper.getBlockState(blockPosition);

            pose.pushPose();
            pose.translate(blockPosition.getX() - (int)position.x, blockPosition.getY() - (int)position.y, blockPosition.getZ() - (int)position.z);

            // Rending the block model with every rendertype used in the model
            if(state.getRenderShape() != RenderShape.INVISIBLE) {

                var model =  mc.getBlockRenderer().getBlockModel(state);
                var modelData = model.getModelData(wrapper, blockPosition, state, null);
                if(modelData == null)
                    modelData = ModelData.EMPTY;

                randomsource.setSeed(state.getSeed(blockPosition));
                for (var renderType : model.getRenderTypes(state, randomsource, modelData)) {
                    mc.getBlockRenderer().renderBatched(state, blockPosition, wrapper, pose, context.bufferSource().getBuffer(renderType), 
                        true, RandomSource.create(), modelData, renderType);
                }
            }

            var blockEntity = wrapper.getBlockEntity(blockPosition);
            if(blockEntity != null) {
                var renderer = mc.getBlockEntityRenderDispatcher().getRenderer(blockEntity);
                if(renderer != null)
                    renderer.render(blockEntity, 0, pose, context.bufferSource(), context.combinedLight(), context.combinedOverlay());
            }
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
    public static void renderContraptionHighlight(RenderContext context, @Nonnull ContraptionWrapper wrapper, @Nonnull BlockPos highlightPosition) {
        var position = wrapper.position();
        var rotation = wrapper.rotation();
        var pose = context.pose();

        var state = wrapper.contraption().getState(highlightPosition);
        
        pose.pushPose();
        pose.translate(position.x, position.y, position.z);
        pose.rotateAround(rotation, 0.5f, 0, 0.5f);
        pose.translate(highlightPosition.getX(), highlightPosition.getY(), highlightPosition.getZ());

        var shape = state.getShape(wrapper, highlightPosition);

        var buffer = context.bufferSource().getBuffer(RenderType.lines());
        LevelRenderer.renderVoxelShape(pose, buffer, shape, 0, 0, 0, 0f, 0f, 0f, 0.5f, false);

        pose.popPose();
    }

    /**
	 * <i>requires an shader with vertexformat of POSITION_COLOR</i>
	 * @param matrix
	 * @param builder
	 * @param detailPoints
	 * @param radius
	 * @param color
	 */
	public static void renderCubeSphereShaderless(PoseStack matrix, float detailPoints, float radius, float[] color) {
		color = Utils.parseColor(color);
		// RenderSystem.disableTexture();

        final var XP = new Vector3f(1, 0, 0);
        final var ZP = new Vector3f(0, 0, 1);
		
		Tesselator tessellator = Tesselator.getInstance();
		BufferBuilder builder = tessellator.getBuilder();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
		builder.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR);
		for(int face = 0; face < 6; ++face) {
			matrix.pushPose();

			switch (face) {
			case 1:
	        	 matrix.mulPose(new Quaternionf(new AxisAngle4d(Math.toRadians(90.0F), XP))); break;
			case 2:
	        	 matrix.mulPose(new Quaternionf(new AxisAngle4d(Math.toRadians(-90.0F), XP))); break;
			case 3:
	        	 matrix.mulPose(new Quaternionf(new AxisAngle4d(Math.toRadians(180.0F), XP))); break;
			case 4:
	        	 matrix.mulPose(new Quaternionf(new AxisAngle4d(Math.toRadians(90.0F), ZP))); break;
			case 5:
	        	 matrix.mulPose(new Quaternionf(new AxisAngle4d(Math.toRadians(-90.0F), ZP))); break;
			default:
				break;
			}
			Matrix4f matrix4f = matrix.last().pose();
            float diff = 1 / (detailPoints);
			for (int i = 0; i < detailPoints; i++) {
            	for(int j = 0; j < detailPoints; j++) {
            		float x = diff * i;
            		float z = diff * j;
            		
            		float minX = x - .5f;
            		float maxX = x - .5f + diff;
            		float minZ = z - .5f;
            		float maxZ = z - .5f + diff;
            		
            		float yOffset = .5f;
            		
            		Vector3f vertex = new Vector3f(minX, yOffset, maxZ);
            		vertex.normalize(); vertex.mul(radius);
					builder.vertex(matrix4f, vertex.x(), vertex.y(), vertex.z()).color(color[0], color[1], color[2], color[3]).endVertex();
					
            		vertex = new Vector3f(maxX, yOffset, maxZ);
            		vertex.normalize(); vertex.mul(radius);
					builder.vertex(matrix4f, vertex.x(), vertex.y(), vertex.z()).color(color[0], color[1], color[2], color[3]).endVertex();
					
            		vertex = new Vector3f(maxX, yOffset, minZ);
            		vertex.normalize(); vertex.mul(radius);
					builder.vertex(matrix4f, vertex.x(), vertex.y(), vertex.z()).color(color[0], color[1], color[2], color[3]).endVertex();

                    
                    vertex = new Vector3f(minX, yOffset, maxZ);
            		vertex.normalize(); vertex.mul(radius);
					builder.vertex(matrix4f, vertex.x(), vertex.y(), vertex.z()).color(color[0], color[1], color[2], color[3]).endVertex();
					
            		vertex = new Vector3f(maxX, yOffset, minZ);
            		vertex.normalize(); vertex.mul(radius);
					builder.vertex(matrix4f, vertex.x(), vertex.y(), vertex.z()).color(color[0], color[1], color[2], color[3]).endVertex();

            		vertex = new Vector3f(minX, yOffset, minZ);
            		vertex.normalize(); vertex.mul(radius);
					builder.vertex(matrix4f, vertex.x(), vertex.y(), vertex.z()).color(color[0], color[1], color[2], color[3]).endVertex();
            	}
			}
			matrix.popPose();
			
			// RenderSystem.enableTexture();
		}
		tessellator.end();
	}

    /**
     * Prepares the given contraption wrapper to be rendered
     * @param wrapper the contraption wrapper to prepare
     * @return the prepared contraption wrapper
     */
    private static ContraptionWrapper prepareWrapper(ContraptionWrapper wrapper) {
        var result = new ContraptionWrapper(
            ContraptionHelper.withOffset(
                wrapper.contraption(), 
                new BlockPos(
                    (int)wrapper.position().x, 
                    (int)wrapper.position().y, 
                    (int)wrapper.position().z)),
            wrapper.internalLevel());
        
        result.setPosition(new Vec3(
            wrapper.position().x() % 1,
            wrapper.position().y() % 1,
            wrapper.position().z() % 1
        ));
        return result;
    }

}
