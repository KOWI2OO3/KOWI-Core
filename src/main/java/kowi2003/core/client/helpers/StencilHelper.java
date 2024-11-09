package kowi2003.core.client.helpers;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;

public class StencilHelper {
    
    /**
     * Sets up the stencil buffer for the next rendering pass.
     */
    public static void setupStencil()
    {
        setupStencil(true);
    }

    public static void finishStencil()
    {
        StencilHelper.setupRenderOutside();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

    /**
     * Sets up the stencil buffer for the next rendering pass.
     * @param clearStencilBuffer if true, the stencil buffer will be cleared before setting up the stencil buffer.
     */
    public static void setupStencil(boolean clearStencilBuffer) {
        if(clearStencilBuffer)
            RenderSystem.clearStencil(0xFF);

        enableStencils();
        setStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);
        clearStencilBuffer();
        setStencilFunc(GL11.GL_ALWAYS, 1, 0xFF); // all fragments should pass the stencil test
        setStencilMask(0xFF);
    }

    /**
     * Sets up the stencil buffer for the next rendering pass in a way which makes it such that the stencil buffer will be empty in the next specified shapes.
     * @param clearStencilBuffer if true, the stencil buffer will be cleared before setting up the stencil buffer.
     */
    public static void setupStencilInverse() {		
        RenderSystem.clearStencil(0xFF);
        
        enableStencils();
        setStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);
        clearStencilBuffer();
        setStencilFunc(GL11.GL_ALWAYS, 0, 0xFF); // all fragments should pass the stencil test
        setStencilMask(0xFF);
        //Draw the Things that need to be replaced!
    }
	
    /**
     * Sets up the stencil buffer for rendering inside the stencil buffer.
     */
	public static void setupRenderInside() {
		setStencilFunc(GL11.GL_EQUAL, 1, 0xFF);
		setStencilMask(0x00); // disable writing to the stencil buffer (because we only want to read the stencil buffer from this point, not write to it anymore)
		RenderSystem.disableDepthTest();
		RenderSystem.depthMask(false);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		//Render whatever is inside of the stencil/window! (portal??)
	}
	
    /**
     * Sets up the stencil buffer for rendering outside the stencil buffer.
     */
	public static void setupRenderOutside() {
		setStencilFunc(GL11.GL_NOTEQUAL, 1, 0xFF);
		setStencilMask(0x00); // disable writing to the stencil buffer (because we only want to read the stencil buffer from this point, not write to it anymore)
		RenderSystem.disableDepthTest();
		RenderSystem.depthMask(false);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		//Render whatever is inside of the stencil/window! (portal??)
	}

    /**
	 * Stencil Info: https://learnopengl.com/Advanced-OpenGL/Stencil-testing
	 */
	public static void enableStencils() {
		enableStencils(true);
	}
	
	/**
	 * Stencil Info: https://learnopengl.com/Advanced-OpenGL/Stencil-testing
     * @param clearStencil if true, the stencil buffer will be cleared after enabling the stencil buffer.
	 */
	private static void enableStencils(boolean clearStencil) {
		GL20.glEnable(GL20.GL_STENCIL_TEST);
		if(!Minecraft.getInstance().getMainRenderTarget().isStencilEnabled()) {
			Minecraft.getInstance().getMainRenderTarget().enableStencil();
		}
		if(clearStencil)
			clearStencilBuffer();
	}

    /**
     * Clears the stencil buffer.
     */
    public static void clearStencilBuffer() {
		RenderSystem.clear(GL11.GL_STENCIL_BUFFER_BIT, false);
//		GL20.glClear(GL20.GL_STENCIL_BUFFER_BIT);
	}

    /**
	 * 0xFF -> each bit is written to the stencil buffer as is,</br>
	 * 0x00 -> each bit ends up as 0 in the stencil buffer (disabling writes)
	 * @param value
	 */
	public static void setStencilMask(int value) {
		RenderSystem.stencilMask(value);
//		GL20.glStencilMask(value);
	}

    /**
	 * @param func	-> GL_NEVER, GL_LESS, GL_LEQUAL, GL_GREATER, GL_GEQUAL, GL_EQUAL, GL_NOTEQUAL, GL_ALWAYS
	 * @param ref	-> the value to compare to (if 1 and the func is GL_EQUAL only parts of the buffer that contain exact 1 will pass)
	 * @param mask	-> the mask to compare (also the applied stencil buffer?) (0xFF)
	 */
	public static void setStencilFunc(int func, int ref, int mask) {
		RenderSystem.stencilFunc(func, ref, mask);
//		GL20.glStencilFunc(func, ref, mask);
	}

    /**
	 *	'SetStencilOperation'</br></br>
	 *  contains three options of which we can specify for each option what action to take:</br><i>
	 *  -sfail: action to take if the stencil test fails.</br>
	 *  -dpfail: action to take if the stencil test passes, but the depth test fails.</br>
	 *  -dppass: action to take if both the stencil and the depth test pass.</i>
	 *  </br></br>
	 *  Then for each of the options you can take any of the following actions:</br>
	 * 
	 * <b>GL_KEEP</b>		The currently stored stencil value is kept.</br>
	 * <b>GL_ZERO</b>		The stencil value is set to 0.</br>
	 * <b>GL_REPLACE</b>	The stencil value is replaced with the reference value set with glStencilFunc.</br>
	 * <b>GL_INCR</b>		The stencil value is increased by 1 if it is lower than the maximum value.</br>
	 * <b>GL_INCR_WRAP</b>	Same as GL_INCR, but wraps it back to 0 as soon as the maximum value is exceeded.</br>
	 * <b>GL_DECR</b>	The stencil value is decreased by 1 if it is higher than the minimum value.</br>
	 * <b>GL_DECR_WRAP</b>	Same as GL_DECR, but wraps it to the maximum value if it ends up lower than 0.</br>
	 * <b>GL_INVERT</b>	Bitwise inverts the current stencil buffer value.</br>
	 * @param sfail
	 * @param dpfail
	 * @param dppass
	 */
	public static void setStencilOp(int sfail, int dpfail, int dppass) {
		RenderSystem.stencilOp(sfail, dpfail, dppass);
//		GL20.glStencilOp(sfail, dpfail, dppass);
	}
	
	public static void resetStencilFunctions() {
		setStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
	}


}
