package kowi2003.core.events;

import org.lwjgl.glfw.GLFW;

import kowi2003.core.Core;
import kowi2003.core.common.colliders.CollisionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent.Stage;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(bus = Bus.FORGE, modid = Core.MODID)
public class DebugEvents {
    
    static boolean isDebug = false;

    /**
     * checks whether an key is down
     * @param key the key id as specified by GLFW <i><br>(keys: GLFW.GLFW_KEY_??</i>)
     * @return
     */
    @OnlyIn(Dist.CLIENT)
    public static boolean isKeyDown(int key) {
        return GLFW.glfwGetKey(Minecraft.getInstance().getWindow().getWindow(), key) == GLFW.GLFW_TRUE;
    }

    @SubscribeEvent
    public static void handleInput(InputEvent event) {
        if(isKeyDown(GLFW.GLFW_KEY_F3) && isKeyDown(GLFW.GLFW_KEY_F7)) {
            isDebug = !isDebug;
        }
    }

    @SubscribeEvent
    public static void render(RenderLevelStageEvent event) {
        if(event.getStage() != Stage.AFTER_SOLID_BLOCKS || !isDebug) return;
        var mc = Minecraft.getInstance();
        var level = mc.level;
        var player = mc.player;

        if(level == null || player == null) return;

        var buffer = mc.renderBuffers().bufferSource();
        var pose = event.getPoseStack();
        var cameraPosition = mc.gameRenderer.getMainCamera().getPosition();
        
        pose.pushPose();
        pose.translate(-cameraPosition.x, -cameraPosition.y, -cameraPosition.z);
        
        // Rendering entity colliders debug
        var entities = level.getEntitiesOfClass(Entity.class, player.getBoundingBox().inflate(20));
        for(var entity : entities) {
            if(entity == player && mc.options.getCameraType().isFirstPerson()) continue;

            var box = entity.getBoundingBox();
            LevelRenderer.renderVoxelShape(pose, buffer.getBuffer(RenderType.lines()), Shapes.create(box),  0, 0, 0, 0f, 0f, 0f, 0.5f, false);

            var spheres = CollisionHelper.convertToSpheres(box);
            for(var sphere : spheres) {
                pose.pushPose();
                pose.translate(sphere.center().x, sphere.center().y, sphere.center().z);
                // RenderHelper.renderCubeSphereShaderless(pose, 10, (float)sphere.radius(), new float[] {1f, 1f, 1f, 1f});
                pose.popPose();
            }
        }

        pose.popPose();
    }

}
