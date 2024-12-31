package io.kowi2003.testing_core.events;

import java.util.ArrayList;
import java.util.HashMap;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import io.kowi2003.testing_core.TestingCore;
import io.kowi2003.testing_core.init.ModModels;
import kowi2003.core.client.animation.Animation;
import kowi2003.core.client.animation.KeyFrame;
import kowi2003.core.client.animation.PartAnimation;
import kowi2003.core.client.helpers.AnimationHelper;
import kowi2003.core.client.helpers.RenderHelper;
import kowi2003.core.client.model.IAnimatedModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent.Stage;
import net.minecraftforge.event.TickEvent.RenderTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(bus = Bus.FORGE, modid = TestingCore.MODID)
public class DebugEvents {
    
    @SubscribeEvent
    public static void onRenderTick(RenderTickEvent event) {
        // event.renderTickTime
    }

    static IAnimatedModel model;
    static Animation animation;

    @SubscribeEvent
    public static void onRenderTick(RenderLevelStageEvent event) {
        
        if(event.getStage() != Stage.AFTER_PARTICLES) return;
        var mc = Minecraft.getInstance();
        var camera = mc.gameRenderer.getMainCamera();
        var cameraPos = camera.getPosition();
        var pose = event.getPoseStack();

        var buffer = mc.renderBuffers().bufferSource();
         
        var light = RenderHelper.getBrightness(new BlockPos(0, 0, 0));

        if(GLFW.glfwGetKey(mc.getWindow().getWindow(), GLFW.GLFW_KEY_MINUS) == GLFW.GLFW_TRUE) {
            var rl = new ResourceLocation(TestingCore.MODID, "animations/animation.json");
            animation = AnimationHelper.loadAnimation(rl);
        }

        if(model == null) {
            model = (IAnimatedModel) ModModels.HELI.get();
        }
        if(animation == null || GLFW.glfwGetKey(mc.getWindow().getWindow(), GLFW.GLFW_KEY_0) == GLFW.GLFW_TRUE) {
            var rotations = new ArrayList<KeyFrame>();
            rotations.add(new KeyFrame(0, new Vector3f(0, 0, 0)));
            rotations.add(new KeyFrame(5, new Vector3f(0, (float)Math.PI, 0)));
            rotations.add(new KeyFrame(10, new Vector3f(0, 2 * (float)Math.PI, 0)));
            rotations.add(new KeyFrame(20, new Vector3f(0, 2 * (float)Math.PI, 0)));
            rotations.add(new KeyFrame(30, new Vector3f(0, (float)Math.PI, 0)));
            
            var position = new ArrayList<KeyFrame>();
            position.add(new KeyFrame(5, new Vector3f(0, 0, 0)));
            position.add(new KeyFrame(10, new Vector3f(0, 2, 0)));
            position.add(new KeyFrame(20, new Vector3f(0, 2, 0)));
            position.add(new KeyFrame(30, new Vector3f(0, 0, 0)));

            
            var scale = new ArrayList<KeyFrame>();
            scale.add(new KeyFrame(10, new Vector3f(1, 1, 1)));
            scale.add(new KeyFrame(15, new Vector3f(2, 2, 2)));
            scale.add(new KeyFrame(20, new Vector3f(1, 1, 1)));

            var map = new HashMap<String, PartAnimation>();
            map.put("Moving", new PartAnimation(position, rotations, scale));
            animation = new Animation(map);
        }

        model.applyAnimation(animation.getTransforms());
        animation.loop(true);
        animation.play();

        pose.pushPose();
        pose.translate(-cameraPos.x(), -cameraPos.y(), -cameraPos.z());
        if(buffer != null) {
           model.render(pose, buffer, RenderType::entityCutout, light, 0, 0);
        }
        pose.popPose();

        animation.increaseTime(event.getPartialTick() / 40f);
    }

}
