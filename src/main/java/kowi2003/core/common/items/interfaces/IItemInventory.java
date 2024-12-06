package kowi2003.core.common.items.interfaces;

/**
 * A simple to use interface to allow for items to have an inventory.
 * all of the capability attaching is done automatically by the core.
 * 
 * compatible with other mods interacting with item inventories through forge capabilities.
 * 
 * @author KOWI2003
 */
public interface IItemInventory {
    
    /**
     * Defines the amount of slots that should be in the inventory capability of this item
     * @return the amount of slots in the inventory
     */
    int getSlotCount();

}
