package kowi2003.core.mixin.contraptions;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import kowi2003.core.entity.entities.ContraptionEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

@Mixin( Player.class )
public class PlayerMixin {
    
	@Inject(method = "attack", at = 
		@At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V"), cancellable = true)
	private void onAttack(Entity entity, CallbackInfo ci) {
		if(entity instanceof ContraptionEntity)
			ci.cancel();
	}
}
