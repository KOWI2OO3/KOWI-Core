package kowi2003.core.common.items;

import kowi2003.core.common.items.interfaces.IItemEnergyStorage;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;

/**
 * A default item implementation with energy capabilities
 */
public class EnergyItem extends DefaultItem implements IItemEnergyStorage {

    private int capacity;
    private int maxReceive;
    private int maxExtract;
    
    /**
     * @param capacity the maximum amount of energy that can be stored in this item
     * @param maxTransfer the maximum amount of energy that can be moved (in/out) in a single tick
     */
    public EnergyItem(int capacity, int maxTransfer) {
        this(capacity, maxTransfer, maxTransfer);
    }

    /**
     * @param capacity the maximum amount of energy that can be stored in this item
     * @param maxReceive the maximum amount of energy that can be received by this item in a single tick
     * @param maxExtract the maximum amount of energy that can be extracted from this item in a single tick
     */
    public EnergyItem(int capacity, int maxReceive, int maxExtract) {
        this.capacity = capacity;
        this.maxReceive = maxReceive;
        this.maxExtract = maxExtract;
    }

    /**
     * @param properties vanilla's item properties
     * @param capacity the maximum amount of energy that can be stored in this item
     * @param maxTransfer the maximum amount of energy that can be moved (in/out) in a single tick
     */
    public EnergyItem(Properties properties, int capacity, int maxTransfer) {
        this(properties, capacity, maxTransfer, maxTransfer);
    }

    /**
     * @param properties vanilla's item properties
     * @param capacity the maximum amount of energy that can be stored in this item
     * @param maxReceive the maximum amount of energy that can be received by this item in a single tick
     * @param maxExtract the maximum amount of energy that can be extracted from this item in a single tick
     */
    public EnergyItem(Properties properties, int capacity, int maxReceive, int maxExtract) {
        super(properties);
        this.capacity = capacity;
        this.maxReceive = maxReceive;
        this.maxExtract = maxExtract;
    }

    @Override
    public int getCapacity() { return capacity; }

    @Override
    public int getMaxRecieve() { return maxReceive; }

    @Override
    public int getMaxExtract() { return maxExtract; }
    
    public static LazyOptional<IEnergyStorage> getEnergyStorage(ItemStack stack) {
        return stack.getCapability(ForgeCapabilities.ENERGY);
    }
}
