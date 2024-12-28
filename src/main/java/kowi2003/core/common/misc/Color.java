package kowi2003.core.common.misc;

/**
 * Represents a color with red, green, blue and alpha values
 */
public record Color(float red, float green, float blue, float alpha) {
   
    /**
     * Creates a new color with the given red, green, blue and alpha values
     * @param red The red value of the color
     * @param green The green value of the color
     * @param blue The blue value of the color
     * @param alpha The alpha value of the color
     */
    public Color(float red, float green, float blue) { this(red, green, blue, 1.0f); }

    /**
     * Creates a new color with the given red, green, blue and alpha values
     * @param color The RGB value of the color
     * @return The color
     */
    public static Color fromRGB(int color) {
        return fromRGB(color, false);
    }

    /**
     * Creates a new color with the given red, green, blue and alpha values
     * @param color The RGB value of the color
     * @param includeAlpha Whether to include the alpha value
     * @return The color
     */
    public static Color fromRGB(int color, boolean includeAlpha) {
        return includeAlpha ? new Color(
            (color >> 16 & 255) / 255.0F,
            (color >> 8 & 255) / 255.0F,
            (color & 255) / 255.0F,
            (color >> 24 & 255) / 255.0F
        ) : new Color(
            (color >> 16 & 255) / 255.0F,
            (color >> 8 & 255) / 255.0F,
            (color & 255) / 255.0F
        );
    }

    /**
     * Creates a new color with the given red, green, blue and alpha values
     * @param red The red value of the color
     * @param green The green value of the color
     * @param blue The blue value of the color
     * @param alpha The alpha value of the color
     * @return The color
     */
    public static Color fromRGB(int red, int green, int blue)
    {
        return new Color(red / 255.0F, green / 255.0F, blue / 255.0F);
    }

    /**
     * Converts the color to a RGB value
     * @return The RGB value of the color
     */
    public int toRGB() {
        return (int)(red * 255) << 16 | (int)(green * 255) << 8 | (int)(blue * 255);
    }

    /**
     * Creates a new color with the given red, green, blue and alpha values
     * @param hex The hex value of the color
     * @return The color
     */
    public static Color fromHex(int hex) {
        return fromHex(hex, false);
    }

    /**
     * Creates a new color with the given red, green, blue and alpha values
     * @param hex The hex value of the color
     * @param includeAlpha Whether to include the alpha value
     * @return The color
     */
    public static Color fromHex(int hex, boolean includeAlpha) {
        return includeAlpha ? new Color(
            (float)(hex >> 16 & 255) / 255.0F,
            (float)(hex >> 8 & 255) / 255.0F,
            (float)(hex & 255) / 255.0F,
            (float)(hex >> 24 & 255) / 255.0F
        ) : new Color(
            (float)(hex >> 16 & 255) / 255.0F,
            (float)(hex >> 8 & 255) / 255.0F,
            (float)(hex & 255) / 255.0F
        );
    }
    
    /**
     * Converts the color to a hex value without alpha
     * @return The hex value of the color
     */
    public int toHex() {
        return toHex(false);
    }

    /**
     * Converts the color to a hex value
     * @param includeAlpha Whether to include the alpha value
     * @return The hex value of the color
     */
    public int toHex(boolean includeAlpha) {
        return (int)(red * 255) << 16 | (int)(green * 255) << 8 | (int)(blue * 255) | (includeAlpha ? (int)(alpha * 255) << 24 : 0);
    }

    /**
     * Creates a new color with the given the hex string of the color
     * @param hex The hex string of the color
     * @return The color
     */
    public static Color fromHexString(String hex) {
        return fromRGB(Integer.parseInt(hex.replace("#", ""), 16), hex.length() > 7);
    }

    /**
     * Converts the color to a hex string without alpha
     * @return The hex string of the color
     */
    public String toHexString() {
        return toHexString(false);
    }

    /**
     * Converts the color to a hex string
     * @param includeAlpha Whether to include the alpha value
     * @return The hex string of the color
     */
    public String toHexString(boolean includeAlpha)
    {
	    String hex = Integer.toHexString(toRGB() & 0xffffff);
        if(!includeAlpha)
            return hex;
		String alphaHex = Integer.toHexString(Math.round(alpha() * 255) & 0xff);
        return alphaHex + hex;
    }
}
