package kowi2003.core.capability.item;

import org.jetbrains.annotations.NotNull;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

/**
 * An Item based inventory implementation for the IItemStackHandler Capability.
 * Allowing items to have an inventory just like blocks can.
 * 
 * @author KOWI2003
 */
public class ItemItemStackHandler extends ItemStackHandler {
    
    ItemStack stack;

    public ItemItemStackHandler(int size, ItemStack stack) {
        super(size);
        this.stack = stack;
    }

    @Override
    public int getSlots() {
        readData();
        return super.getSlots();
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot) {
        readData();
        return super.getStackInSlot(slot);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        readData();
        return super.isItemValid(slot, stack);
    }

    @Override
    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
        readData();
        super.setStackInSlot(slot, stack);
        saveData();
    }

    @Override
    public void setSize(int size) {
        readData();
        super.setSize(size);
        saveData();
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        readData();
        var result = super.extractItem(slot, amount, simulate);
        saveData();
        return result;
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        readData();
        var result = super.insertItem(slot, stack, simulate);
        saveData();
        return result;
    }

    @Override
    protected void validateSlotIndex(int slot) {
		readData();
		super.validateSlotIndex(slot);
    }

	@Override
	public int getSlotLimit(int slot) {
		readData();
		return super.getSlotLimit(slot);
	}
	
	@Override
    protected int getStackLimit(int slot, @NotNull ItemStack stack) {
        readData();
        return super.getStackLimit(slot, stack);
    }

    @Override
    protected void onContentsChanged(int slot) {
        super.onContentsChanged(slot);
        saveData();
    }

    /**
     * reads the data from the item stack
     */
    private void readData() {
		CompoundTag tag = getStack().getOrCreateTag();
		if(tag.contains("Energy"))
			deserializeNBT(tag.getCompound("Energy"));
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

    ItemStack getStack() {
        return stack;
    }
}
