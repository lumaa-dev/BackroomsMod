package com.lumination.backrooms.items.interactables;

import com.lumination.backrooms.blocks.ModBlocks;
import com.lumination.backrooms.blocks.interactable.TapePlayer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.sound.SoundEvent;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;

public class MusicTapes extends MusicDiscItem {

    public MusicTapes(int comparatorOutput, SoundEvent sound, Settings settings, int lengthInSeconds) {
        super(comparatorOutput, sound, settings, lengthInSeconds);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockPos blockPos;
        World world = context.getWorld();
        BlockState blockState = world.getBlockState(blockPos = context.getBlockPos());
        if (!blockState.isOf(ModBlocks.TAPE_PLAYER) || blockState.get(TapePlayer.HAS_RECORD).booleanValue()) {
            return ActionResult.PASS;
        }
        ItemStack itemStack = context.getStack();
        if (!world.isClient) {
            ((TapePlayer) ModBlocks.TAPE_PLAYER).setRecord(context.getPlayer(), world, blockPos, blockState, itemStack);
            world.syncWorldEvent(null, WorldEvents.MUSIC_DISC_PLAYED, blockPos, Item.getRawId(this));
            itemStack.decrement(1);
            PlayerEntity playerEntity = context.getPlayer();
            if (playerEntity != null) {
                playerEntity.incrementStat(Stats.PLAY_RECORD);
            }
        }
        return ActionResult.success(world.isClient);
    }
}
