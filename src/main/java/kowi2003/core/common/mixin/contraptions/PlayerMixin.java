package kowi2003.core.common.mixin.contraptions;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import kowi2003.core.common.entities.entity.ContraptionEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

/**
 * A mixin class injected into the player 
 * 
 * @author KOWI2003
 */
@Mixin( Player.class )
public class PlayerMixin {
    
	/**
	 * A simple mixin to prevent the sound of an entity being hit to trigger when the player interacts/hits the contraption
	 * @param entity the entity being attacked
	 * @param ci the callback info
	 */
	@Inject(method = "attack", at = 
		@At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V"), cancellable = true)
	private void onAttack(Entity entity, CallbackInfo ci) {
		if(entity instanceof ContraptionEntity)
			ci.cancel();
	}
}
