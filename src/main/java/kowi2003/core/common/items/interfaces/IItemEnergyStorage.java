package kowi2003.core.common.items.interfaces;

public interface IItemEnergyStorage {

	int getCapacity();
	
	int getMaxRecieve();
	
	int getMaxExtract();
	
	default int getDefaultEnergy() {
		return getCapacity();
	}
	
}
