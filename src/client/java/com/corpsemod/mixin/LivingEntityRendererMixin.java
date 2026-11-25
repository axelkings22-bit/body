package com.corpsemod.mixin;

import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin {
	/**
	 * Oscurece la skin de los jugadores muertos. Es opcional: con require = 0,
	 * si este metodo cambia de nombre en tu version, el mod sigue funcionando
	 * y solo se pierde el oscurecido.
	 */
	@Inject(method = "getMixColor", at = @At("RETURN"), cancellable = true, require = 0)
	private void deadbodies$darken(LivingEntityRenderState state, CallbackInfoReturnable<Integer> cir) {
		if (state instanceof PlayerEntityRenderState && state.deathTime > 0) {
			cir.setReturnValue(0xFF555555);
		}
	}
}
