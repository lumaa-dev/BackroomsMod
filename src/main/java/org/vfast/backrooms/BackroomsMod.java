package org.vfast.backrooms;

import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import org.quiltmc.qsl.lifecycle.api.event.ServerLifecycleEvents;
import org.vfast.backrooms.block.BackroomsBlocks;
import org.vfast.backrooms.block.entity.BackroomsBlockEntities;
import org.vfast.backrooms.block.interactable.Radio;
import org.vfast.backrooms.config.BackroomsConfig;
import org.vfast.backrooms.entity.BackroomsEntities;
import org.vfast.backrooms.entity.BacteriaEntity;
import org.vfast.backrooms.item.BackroomsItems;
import org.vfast.backrooms.network.BackroomsNetworking;
import org.vfast.backrooms.sound.BackroomsSounds;
import org.vfast.backrooms.world.biome.BackroomsBiomes;
import org.vfast.backrooms.world.BackroomsDimensions;
import org.vfast.backrooms.world.chunk.BackroomsChunkGenerators;
import net.ludocrypt.limlib.api.LimlibTravelling;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.TeleportTarget;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.loader.api.QuiltLoader;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.entity.event.api.EntityWorldChangeEvents;
import org.quiltmc.qsl.entity.event.api.ServerEntityTickCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.bernie.geckolib.GeckoLib;

import java.util.Arrays;
import java.util.List;

public class BackroomsMod implements ModInitializer {

	public static final String ID = "backrooms";

	public static String NAME = "The Backrooms";

	public static final Logger LOGGER = LoggerFactory.getLogger(NAME);

	private static final String VERSION_ID = QuiltLoader
			.getModContainer(BackroomsMod.ID)
			.orElseThrow()
			.metadata()
			.version()
			.toString();

	private static final List<Radio.RadioRecord> records = Arrays.asList(
			new Radio.RadioRecord(Text.translatable("item.backrooms.halls_tape.desc").getString(), BackroomsSounds.HALLS),
			new Radio.RadioRecord(Text.translatable("item.backrooms.duet_tape.desc").getString(), BackroomsSounds.DUET),
			new Radio.RadioRecord(Text.translatable("item.backrooms.drifting_tape.desc").getString(), BackroomsSounds.DRIFTING),
			new Radio.RadioRecord(Text.translatable("item.backrooms.no_time_to_explain_tape.desc").getString(), BackroomsSounds.NO_TIME_TO_EXPLAIN),
			new Radio.RadioRecord(Text.translatable("item.backrooms.tell_me_you_know_tape.desc").getString(), BackroomsSounds.TELL_ME_YOU_KNOW),
			new Radio.RadioRecord(Text.translatable("item.backrooms.orbit_tape.desc").getString(), BackroomsSounds.ORBIT),
			new Radio.RadioRecord(Text.translatable("item.backrooms.slingshot_tape.desc").getString(), BackroomsSounds.SLINGSHOT),
			new Radio.RadioRecord(Text.translatable("item.backrooms.thalassophobia_tape.desc").getString(), BackroomsSounds.THALASSOPHOBIA ),
			new Radio.RadioRecord(Text.translatable("item.backrooms.no_surprises_tape.desc").getString(), BackroomsSounds.NO_SURPRISES),
			new Radio.RadioRecord(Text.translatable("item.backrooms.its_just_a_burning_memory_tape.desc").getString(), BackroomsSounds.BURNING_MEMORY),
			new Radio.RadioRecord(Text.translatable("item.backrooms.government_funding_tape.desc").getString(), BackroomsSounds.GOVERNMENT_FUNDING )
	);

	@Override
	public void onInitialize(ModContainer mod) {
		BackroomsConfig.HANDLER.load();
		BackroomsBlocks.registerModBlock();
		BackroomsBlockEntities.registerBlockEntities();
		BackroomsItems.registerModItems();
		BackroomsSounds.registerSoundEvents();
		BackroomsBiomes.registerBiomes();
		BackroomsChunkGenerators.registerChunkGenerators();
		BackroomsNetworking.registerPackets();
		GeckoLib.initialize();
		BackroomsEntities.registerMobs();
		registerEvents();
		BackroomsMod.LOGGER.info("Initialized Backrooms");
	}

	public void registerEvents() {
		ServerLifecycleEvents.STOPPING.register((server) -> {
			BackroomsConfig.HANDLER.save();
		});
		EntityWorldChangeEvents.AFTER_PLAYER_WORLD_CHANGE.register((player, origin, destination) -> {
			if (destination == player.getServer().getWorld(BackroomsDimensions.LEVEL_ZERO_KEY)) {
				StatusEffectInstance effect = new StatusEffectInstance(StatusEffects.MINING_FATIGUE, StatusEffectInstance.INFINITE, 2, false, false, false);
				player.addStatusEffect(effect);
				player.setSpawnPoint(BackroomsDimensions.LEVEL_ZERO_KEY,
						player.getBlockPos(), // TODO make spawnpoint position randomized
						0.0f, true, false);
			} else if (origin == player.getServer().getWorld(BackroomsDimensions.LEVEL_ZERO_KEY)){
				player.removeStatusEffect(StatusEffects.MINING_FATIGUE);
			}
		});
		ServerEntityTickCallback.EVENT.register((entity, isPassengerTick) -> {
			Random rand = entity.getWorld().getRandom();
			if ((entity.isInsideWall() && rand.nextBetween(1, 50) == 50) || (rand.nextBetween(1, 36000) == 36000 && entity.isPlayer() && entity.getWorld() != entity.getServer().getWorld(BackroomsDimensions.LEVEL_ZERO_KEY) && !(((ServerPlayerEntity)entity).isCreative() || ((ServerPlayerEntity)entity).isSpectator()))) {
				LimlibTravelling.travelTo(entity, entity.getServer().getWorld(BackroomsDimensions.LEVEL_ZERO_KEY), new TeleportTarget(
								Vec3d.of(new Vec3i(rand.nextBetween(entity.getBlockX()-200, entity.getBlockX()+200), 2, rand.nextBetween(entity.getBlockZ()-200, entity.getBlockZ()+200))),
								Vec3d.ZERO, 0.0f, 0.0f),
						BackroomsSounds.CAMERA_CLICK, 5.0f, 1.0f);
			}
		});
		AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
			if (player.getStackInHand(hand).isOf(BackroomsItems.BROKEN_BOTTLE)) {
				BacteriaEntity bacteria = (BacteriaEntity) entity;
				StatusEffectInstance effect = new StatusEffectInstance(StatusEffects.SLOWNESS, 5, 2, false, false, false);
				bacteria.addStatusEffect(effect);
			}
			return ActionResult.PASS;
		});
	}

	public static List<Radio.RadioRecord> getRecords() {
		return records;
	}
}
