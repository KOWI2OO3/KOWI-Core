package kowi2003.core.common.items.interfaces;

import javax.annotation.Nonnull;

import net.minecraftforge.fluids.FluidStack;

public interface IItemFluidContainer {

	int getContainerCapacity();
	
	default boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
		return true;
	}
}
