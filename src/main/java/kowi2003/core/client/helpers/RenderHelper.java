package kowi2003.core.client.helpers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

/**
 * A helper class for rendering, from simple to complex rendering tasks
 * 
 * @author KOWI2003
 */
public final class RenderHelper 
{   
    /**
     * The full brightness value. Can be used as a standin for combined light
     */
    public static final int FULL_BRIGHTNESS = 0xFFFFFF;

    /**
     * The no overlay value. Can be used as a standin for combined overlay
     */
    public static final int NO_OVERLAY = OverlayTexture.NO_OVERLAY;

    /**
     * Gets the brightness at the given position
     * @param position the position to get the brightness from
     * @return the brightness at the given position
     */
    public static int getBrightness(BlockPos position)
    {
        var mc = Minecraft.getInstance();
        var level = mc != null ? mc.level : null;
        return level != null ? getBrightness(level, position) : FULL_BRIGHTNESS;
    }

    /**
     * Gets the brightness at the given position
     * @param level the level to get the brightness from
     * @param position the position to get the brightness from
     * @return the brightness at the given position
     */
    public static int getBrightness(Level level, BlockPos position)
    {
        return LevelRenderer.getLightColor(level, position);
    }

}
