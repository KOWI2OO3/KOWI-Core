package kowi2003.core.common.capabilities;

import org.jetbrains.annotations.NotNull;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class ItemItemStackHandler extends ItemStackHandler {

    private static final String NBT_TAG = "Inventory";

    private ItemStack stack;

    public ItemItemStackHandler(ItemStack stack, int size) {
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
        var stackResult = super.extractItem(slot, amount, simulate);
        saveData();
        return stackResult;
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        readData();
        var stackResult = super.insertItem(slot, stack, simulate);
        saveData();
        return stackResult;
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

    private void readData()
    {
        var tag = getStack().getOrCreateTag();
        if(tag.contains(NBT_TAG))
            deserializeNBT(tag.getCompound(NBT_TAG));
        else
            saveData();
    }

    private void saveData()
    {
        getStack().getOrCreateTag().put(NBT_TAG, serializeNBT());
    }

    public ItemStack getStack() {
        return stack;
    }

}
