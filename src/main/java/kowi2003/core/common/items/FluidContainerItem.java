package kowi2003.core.common.items;

import java.util.function.BiFunction;

import javax.annotation.Nonnull;

import kowi2003.core.common.items.interfaces.IItemFluidContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

public class FluidContainerItem extends DefaultItem implements IItemFluidContainer {

    private int capacity;
    private BiFunction<Integer, FluidStack, Boolean> isFluidValid;

    /**
     * @param capacity the maximum amount of fluid that can be stored in this item
     * @param isFluidValid a function defining if a fluid can be stored in this container at all (ignoring the current amount of fluid) [params: tank, fluidstack]
     */
    public FluidContainerItem(int capacity, BiFunction<Integer, FluidStack, Boolean> isFluidValid) {
        this(capacity);
        this.isFluidValid = isFluidValid;
    }

    /**
     * @param properties vanilla's item properties
     * @param capacity the maximum amount of fluid that can be stored in this item
     * @param isFluidValid a function defining if a fluid can be stored in this container at all (ignoring the current amount of fluid) [params: tank, fluidstack]
     */
    public FluidContainerItem(Properties properties, int capacity,
            BiFunction<Integer, FluidStack, Boolean> isFluidValid) {
        this(properties, capacity);
        this.isFluidValid = isFluidValid;
    }

    /**
     * @param capacity the maximum amount of fluid that can be stored in this item
     */
    public FluidContainerItem(int capacity) {
        this.capacity = capacity;
    }

    /**
     * @param properties vanilla's item properties
     * @param capacity the maximum amount of fluid that can be stored in this item
     */
    public FluidContainerItem(Properties properties, int capacity) {
        super(properties);
        this.capacity = capacity;
    }

    @Override
    public int getContainerCapacity() { return capacity; }

    @Override
    public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
        return isFluidValid.apply(tank, stack);
    }
    
    public static LazyOptional<IFluidHandlerItem> getFluidContainer(ItemStack stack) {
        return stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM);
    }
}
