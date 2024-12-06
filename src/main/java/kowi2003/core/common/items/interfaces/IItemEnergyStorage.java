package kowi2003.core.common.items.interfaces;

/**
 * A simple to use interface to allow for items to store energy.
 * all of the capability attaching is done automatically by the core.
 * 
 * The entire energy implementation is compatible with forge energy
 * 
 * @author KOWI2003
 */
public interface IItemEnergyStorage {

	/**
     * the maximum amount of energy to be stored in the item
     * @return the maximum amount of energy stored
     */
	int getCapacity();
	
    /**
     * the maximum amount of energy this item can recieve in a tick
     * @return the amount of energy per tick
     */
	int getMaxRecieve();
	
    /**
     * the maximum amount of energy that can be extracted from this item in a tick
     * @return the amount of energy per tick
     */
	int getMaxExtract();

	/**
     * the default amount of energy for this item, usually set to 0 or to the capacity
     * @return the default amount of energy on item creation
     */
	default int getDefaultEnergy() {
		return getCapacity();
	}
	
}
