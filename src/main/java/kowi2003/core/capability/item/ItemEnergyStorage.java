package kowi2003.core.capability.item;

import javax.annotation.Nonnull;

import kowi2003.core.items.interfaces.IItemEnergyStorage;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.energy.EnergyStorage;

/**
 * An Item based energy storage implementation
 * Allowing items to store energy just like blocks can.
 * 
 * @author KOWI2003
 */
public class ItemEnergyStorage extends EnergyStorage {
 
    ItemStack stack;

    /**
     * a simple and clean constructor to use the interface supplied in the core to attach 
     * the energy capability to the item stack 
     */
    public ItemEnergyStorage(IItemEnergyStorage storage, ItemStack stack) {
		this(storage.getCapacity(), storage.getMaxRecieve(), storage.getMaxExtract(), storage.getDefaultEnergy(), stack);
	}


	public ItemEnergyStorage(int capacity, int maxReceive, int maxExtract, int energy, ItemStack stack) {
		super(capacity, maxReceive, maxExtract, energy);
		this.stack = stack;
	}

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        readData();
        int i = super.receiveEnergy(maxReceive, simulate);
        saveData();
        return i;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        readData();
        int i = super.extractEnergy(maxExtract, simulate);
        saveData();
        return i;
    }

    @Override
    public int getEnergyStored() {
        readData();
        return super.getEnergyStored();
    }

    @Override
    @SuppressWarnings("null")
    public @Nonnull Tag serializeNBT() {
        return IntTag.valueOf(this.energy);
    }

    /**
     * reads the data from the item stack
     */
    private void readData() {
        var tag = getStack().getOrCreateTag();
        if(tag.contains("Energy"))
            deserializeNBT(tag.get("Energy"));
        else
            saveData();
    }

    /**
     * saves the data to the item stack
     */
    private void saveData() {
        var tag = getStack().getOrCreateTag();
        tag.put("Energy", serializeNBT());
    }
    public ItemStack getStack() {
        return stack;
    }
    
}
