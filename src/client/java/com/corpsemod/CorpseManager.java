package com.corpsemod;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Crea y elimina los cuerpos. Todo ocurre solo en tu cliente: el servidor
 * no sabe nada de estos cuerpos, por eso funciona en cualquier servidor.
 */
public final class CorpseManager {
	/** Cuanto dura el cuerpo en el suelo, en ticks (20 ticks = 1 segundo). */
	public static final int LIFETIME_TICKS = 20 * 10;

	private static final List<Corpse> CORPSES = new ArrayList<>();
	private static long now = 0;
	// Ids negativos para no chocar nunca con los ids reales del servidor.
	private static int nextId = -1_000_000;

	private record Corpse(OtherClientPlayerEntity entity, ClientWorld world, long expiresAt) {}

	private CorpseManager() {}

	public static void spawnCorpse(OtherClientPlayerEntity victim) {
		ClientWorld world = MinecraftClient.getInstance().world;
		if (world == null) {
			return;
		}

		OtherClientPlayerEntity corpse = new OtherClientPlayerEntity(world, victim.getGameProfile());
		corpse.setId(nextId--);
		corpse.refreshPositionAndAngles(victim.getX(), victim.getY(), victim.getZ(), victim.getYaw(), victim.getPitch());
		corpse.bodyYaw = victim.bodyYaw;
		corpse.prevBodyYaw = victim.bodyYaw;
		corpse.headYaw = victim.headYaw;
		corpse.prevHeadYaw = victim.headYaw;

		for (EquipmentSlot slot : EquipmentSlot.values()) {
			corpse.equipStack(slot, victim.getEquippedStack(slot).copy());
		}

		// Con vida 0 el juego sigue la animacion normal de caida y la deja tirada.
		corpse.setHealth(0.0F);
		corpse.deathTime = victim.deathTime;

		world.addEntity(corpse);
		CORPSES.add(new Corpse(corpse, world, now + LIFETIME_TICKS));
	}

	public static void tick(MinecraftClient client) {
		now++;
		ClientWorld current = client.world;
		if (current == null) {
			CORPSES.clear();
			return;
		}

		Iterator<Corpse> it = CORPSES.iterator();
		while (it.hasNext()) {
			Corpse c = it.next();
			if (c.world() != current) {
				// Cambio de dimension o de mundo: esos cuerpos ya no existen.
				it.remove();
			} else if (now >= c.expiresAt()) {
				current.removeEntity(c.entity().getId(), Entity.RemovalReason.DISCARDED);
				it.remove();
			}
		}
	}

	public static void clear() {
		CORPSES.clear();
	}
}
