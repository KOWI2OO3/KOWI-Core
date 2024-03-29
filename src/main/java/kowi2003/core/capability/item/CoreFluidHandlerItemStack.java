package kowi2003.core.capability.item;

import java.util.function.BiFunction;

import org.jetbrains.annotations.NotNull;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;

/**
 * An Item based fluid container implementation
 * Allowing items to store fluids just as a tank would.
 * 
 * @author KOWI2003
 */
public class CoreFluidHandlerItemStack extends FluidHandlerItemStack {

	BiFunction<Integer, FluidStack, Boolean> fluidvalidator;

    public CoreFluidHandlerItemStack(@NotNull ItemStack container, int capacity) {
        this(container, capacity, (tank, stack) -> true);
    }

    public CoreFluidHandlerItemStack(@NotNull ItemStack container, int capacity, BiFunction<Integer, FluidStack, Boolean> fluidvalidator) {
        super(container, capacity);
        this.fluidvalidator = fluidvalidator;
    }
    
	@Override
	public boolean isFluidValid(int tank, FluidStack stack) {
		return fluidvalidator.apply(tank, stack);
	}
}
