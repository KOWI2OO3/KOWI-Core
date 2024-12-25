package kowi2003.core.common.mixin.contraptions;

import kowi2003.core.common.entities.entity.ContraptionEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

/**
 * A helper class to facilitate the mixin required to make contraptions work
 * @author KOWI2003
 */
public final class ContraptionMixinHelper {
    
    /**
     * A helper method to facilitate the modification of the still valid function on containers to account for contraptions
     * @param instance the container menu instance to check
     * @param player the player to check the validity for 
     * @return the new value to return for the still valid function
     */
    public static boolean stillValid(AbstractContainerMenu instance, Player player) {
        // The original value returned in the normal vanilla situation 
        var original = instance.stillValid(player);
        var level = player.level();
		if(level == null) return original;
		var entities = level.getEntitiesOfClass(ContraptionEntity.class, player.getBoundingBox().inflate(player.getBlockReach()));
		return entities.size() > 0 || original;
    }

}
