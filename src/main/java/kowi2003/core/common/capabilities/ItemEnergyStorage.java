package kowi2003.core.common.capabilities;

import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.energy.EnergyStorage;

public class ItemEnergyStorage extends EnergyStorage {
    
    private static final String NBT_TAG = "Energy";
    private ItemStack stack;

    public ItemEnergyStorage(ItemStack stack, int capacity, int maxReceive, int maxExtract) {
        super(capacity, maxReceive, maxExtract);
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

    private void readData()
    {
        var tag = getStack().getOrCreateTag();
        if(tag.contains(NBT_TAG))
            deserializeNBT(tag.get(NBT_TAG));
        else
            saveData();
    }

    private void saveData()
    {
        getStack().getOrCreateTag().put(NBT_TAG, serializeNBT());
    }

    @Override
	public Tag serializeNBT() {
        return IntTag.valueOf(this.energy);
	}

    public ItemStack getStack() {
        return stack;
    }

}
