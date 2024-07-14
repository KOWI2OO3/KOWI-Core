package kowi2003.core.mixin.contraptions;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

@Mixin({ ServerPlayer.class, Player.class })
public class ServerPlayerMixin {
    
	@Redirect(method = "tick", at = @At(value = "INVOKE", target="Lnet/minecraft/world/inventory/AbstractContainerMenu;stillValid(Lnet/minecraft/world/entity/player/Player;)Z"))
	private boolean onStillValid(AbstractContainerMenu instance, Player player) {
		return ContraptionMixinHelper.stillValid(instance, player);
	}

}
