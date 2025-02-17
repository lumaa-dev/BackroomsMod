package org.vfast.backrooms.sound;

import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import org.vfast.backrooms.BackroomsMod;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class BackroomsSounds {
    public static SoundEvent LIGHT_BUZZING = registerSoundEvent("light_buzzing");
    public static SoundEvent CAMERA_CLICK = registerSoundEvent("camera_click");

    // Music Tapes
    public static SoundEvent PAPER_BIRCH = registerSoundEvent("paper_birch");
    public static SoundEvent EMPYREA = registerSoundEvent("empyrea");
    public static SoundEvent LACEBARK_PINE = registerSoundEvent("lacebark_pine");
    public static SoundEvent CURIOSITY = registerSoundEvent("curiosity");

    // Ambient sounds
    public static final SoundEvent ROOM_WIND = registerSoundEvent("room_wind");
    public static final SoundEvent LIGHT_DROPS = registerSoundEvent("light_drops");
    public static final SoundEvent OCEAN = registerSoundEvent("ocean");

    // Monster sounds
    public static final SoundEvent MONSTER_NOISE_1 = registerSoundEvent("monster_noise_1");


    private static SoundEvent registerSoundEvent(String name) {
        Identifier id = Identifier.of(BackroomsMod.ID, name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    public static void registerSoundEvents() {}
}
