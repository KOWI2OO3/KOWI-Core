package kowi2003.core.common.items;

import kowi2003.core.common.items.interfaces.IItemColorable;
import kowi2003.core.common.misc.Color;
import net.minecraft.world.item.ItemStack;

public class ColorableItem extends DefaultItem implements IItemColorable {
    
    private Color defaultColor = Color.fromHex(0xFFFFFF);

    /**
     * Creates an colorable item with the default color: white
     */
    public ColorableItem() {}

    /**
     * Creates an colorable item with the default color: white
     * @param properties vanilla's item properties
     */
    public ColorableItem(Properties properties) {
        super(properties);
    }

    /**
     * @param defaultColor the default color of the item
     */
    public ColorableItem(Color defaultColor) {
        this.defaultColor = defaultColor;
    }

    /**
     * @param properties vanilla's item properties
     * @param defaultColor the default color of the item
     */
    public ColorableItem(Properties properties, Color defaultColor) {
        super(properties);
        this.defaultColor = defaultColor;
    }

    @Override
    public ItemStack getDefaultInstance() {
        var instance = super.getDefaultInstance();
        setColor(instance, defaultColor.red(), defaultColor.green(), defaultColor.blue());
        return instance;
    }

}
