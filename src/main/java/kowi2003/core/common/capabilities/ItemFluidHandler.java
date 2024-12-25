package kowi2003.core.common.capabilities;

import java.util.function.BiFunction;

import org.jetbrains.annotations.NotNull;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;

public class ItemFluidHandler extends FluidHandlerItemStack {
    
    private BiFunction<Integer, FluidStack, Boolean> fluidValidator;

    public ItemFluidHandler(ItemStack container, int capacity) {
        this(container, capacity, (tank, fluidStack) -> true);
    }

    public ItemFluidHandler(ItemStack container, int capacity, BiFunction<Integer, FluidStack, Boolean> fluidValidator) {
        super(container, capacity);
        this.fluidValidator = fluidValidator;
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        return fluidValidator != null ? fluidValidator.apply(tank, stack) : super.isFluidValid(tank, stack);
    }

}
