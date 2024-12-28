package kowi2003.core.common.items;

import kowi2003.core.common.items.interfaces.IItemInventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;

public class InventoryItem extends DefaultItem implements IItemInventory {

    private int slotCount;

    /**
     * @param slotCount the amount of slots in the inventory
     */
    public InventoryItem(int slotCount) {
        this.slotCount = slotCount;
    }

    /**
     * @param properties vanilla's item properties
     * @param slotCount the amount of slots in the inventory
     */
    public InventoryItem(Properties properties, int slotCount) {
        super(properties);
        this.slotCount = slotCount;
    }

    @Override
    public int getSlotCount() { return slotCount; }

    public static LazyOptional<IItemHandler> getInventory(ItemStack stack) {
        return stack.getCapability(ForgeCapabilities.ITEM_HANDLER);
    }
    
}
