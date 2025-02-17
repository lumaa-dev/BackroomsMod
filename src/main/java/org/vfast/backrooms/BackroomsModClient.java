package org.vfast.backrooms;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class BackroomsModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        FabricLoader.getInstance().getModContainer(BackroomsMod.ID).ifPresent(modContainer -> {
            ResourceManagerHelper.registerBuiltinResourcePack(Identifier.of(BackroomsMod.ID, "old_ssc"), modContainer, Text.translatable("resourcePack.old_ssc.name"), ResourcePackActivationType.NORMAL);
            ResourceManagerHelper.registerBuiltinResourcePack(Identifier.of(BackroomsMod.ID, "def_pano"), modContainer, Text.translatable("resourcePack.def_pano.name"), ResourcePackActivationType.NORMAL);
        });
        BackroomsMod.LOGGER.info("Initialized Client Backrooms");
    }
}