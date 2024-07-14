package kowi2003.core.mixin.contraptions;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

/**
 * A mixin class injected into the server player to facilitate contraptions
 * 
 * @author KOWI2003
 */
@Mixin({ ServerPlayer.class, Player.class })
public class ServerPlayerMixin {
    
	/**
	 * A mixin to redirect the still valid function to account for contraptions
	 * @param instance the container menu instance to check
	 * @param player the player to check the validity for
	 * @return the new value to return for the still valid function
	 */
	@Redirect(method = "tick", at = @At(value = "INVOKE", target="Lnet/minecraft/world/inventory/AbstractContainerMenu;stillValid(Lnet/minecraft/world/entity/player/Player;)Z"))
	private boolean onStillValid(AbstractContainerMenu instance, Player player) {
		return ContraptionMixinHelper.stillValid(instance, player);
	}

}
