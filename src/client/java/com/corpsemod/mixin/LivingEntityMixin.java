package com.corpsemod.mixin;

import com.corpsemod.CorpseManager;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
	/** El servidor manda el estado 3 cuando una entidad viva muere. */
	@Inject(method = "handleStatus", at = @At("HEAD"))
	private void deadbodies$onStatus(byte status, CallbackInfo ci) {
		if (status == 3 && (Object) this instanceof OtherClientPlayerEntity victim) {
			CorpseManager.spawnCorpse(victim);
		}
	}
}
