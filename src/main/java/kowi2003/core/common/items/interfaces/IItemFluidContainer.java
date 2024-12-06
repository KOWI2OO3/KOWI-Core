package kowi2003.core.common.items.interfaces;

import javax.annotation.Nonnull;

import net.minecraftforge.fluids.FluidStack;

/**
 * A simple to use interface to allow for items to store fluids.
 * all of the capability attaching is done automatically by the core.
 * 
 * compatible with other mods interacting with item fluid containers through forge capabilities.
 * 
 * @author KOWI2003
 */
public interface IItemFluidContainer {
    
    /**
     * Defines the capacity of the items fluid tank
     * @return the capacity of the tank
     */
	int getContainerCapacity();
	
    /**
     * Defines whether a certain fluid is valid or not.
	 *
	 * this is only a filter of which fluids are allowed in the first place, 
	 * this method should not check if the fluid matches the current fluid in the tank
	 *
     * which fluids can enter the tank in the first place
     * @param tank the tank in which the fluid is supposed to go (Note this is basically always 0 so it can mostly be ignored)
     * @param stack the fluid stack that is being checked whether is is allowed in this item's tank
     * @return whether the fluid stack is allowed in the item's tank
     */
	default boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
		return true;
	}

}
