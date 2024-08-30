package com.github.argon4w.hotpot.items;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.IHotpotTablewareContainer;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.blocks.IHotpotPlacementContainer;
import com.github.argon4w.hotpot.placements.HotpotPlacedSpoon;
import com.github.argon4w.hotpot.placements.HotpotPlacementSerializers;
import com.github.argon4w.hotpot.soups.HotpotSoupStatus;
import com.github.argon4w.hotpot.soups.components.HotpotSoupComponentTypeSerializers;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;

public class HotpotSpoonItem extends HotpotPlacementBlockItem<HotpotPlacedSpoon> implements IHotpotTablewareItem {
    private final HotpotSoupStatus soupStatus;

    public HotpotSpoonItem(HotpotSoupStatus soupStatus) {
        super(HotpotPlacementSerializers.PLACED_SPOON_SERIALIZER, new Properties().stacksTo(1));
        this.soupStatus = soupStatus;
    }

    @Override
    public boolean canPlace(Player player, InteractionHand hand, LevelBlockPos pos) {
        return player.isCrouching() || player.isPassenger();
    }

    @Override
    public void loadPlacement(IHotpotPlacementContainer container, LevelBlockPos pos, HotpotPlacedSpoon placement, ItemStack itemStack) {
        placement.setSpoonItemSlot(itemStack);
    }

    @Override
    public void interact(int hitPos, int layer, Player player, InteractionHand hand, ItemStack itemStack, IHotpotTablewareContainer blockEntity, LevelBlockPos selfPos) {
        if (!(blockEntity instanceof HotpotBlockEntity hotpotBlockEntity)) {
            return;
        }

        if (!hotpotBlockEntity.getSoup().hasComponentType(HotpotSoupComponentTypeSerializers.CAN_BE_PACKED_SOUP_COMPONENT_TYPE_SERIALIZER)) {
            return;
        }

        if (!(itemStack.getItem() instanceof HotpotSpoonItem hotpotSpoonItem)) {
            return;
        }

        HotpotSoupStatus soupStatus = hotpotSpoonItem.getSoupStatus();
        ItemStack offhandItemStack = player.getItemInHand(InteractionHand.OFF_HAND);

        hotpotBlockEntity.setVelocity(soupStatus.getStirringSpeed());

        if (!offhandItemStack.is(HotpotModEntry.HOTPOT_PAPER_BOWL)) {
            return;
        }

        if (!HotpotPaperBowlItem.isPaperBowlClear(offhandItemStack)) {
            return;
        }

        ArrayList<ItemStack> contents = new ArrayList<>();
        ArrayList<ItemStack> skewers = new ArrayList<>();

        for (int i = 0; i < 8; i ++) {
            ItemStack content = blockEntity.getContentByTableware(player, hand, i, 0, selfPos);

            if (content.isEmpty()) {
                continue;
            }

            if (content.is(HotpotModEntry.HOTPOT_SPICE_PACK)) {
                selfPos.dropItemStack(content);
                continue;
            }

            if (content.is(HotpotModEntry.HOTPOT_CHOPSTICK)) {
                selfPos.dropItemStack(content);
                continue;
            }

            if (content.is(HotpotModEntry.HOTPOT_PAPER_BOWL)) {
                selfPos.dropItemStack(content);
                continue;
            }

            if (!content.getItem().canFitInsideContainerItems()) {
                selfPos.dropItemStack(content);
                continue;
            }

            if (content.getItem() instanceof HotpotSkewerItem)  {
                skewers.add(content);
                continue;
            }

            contents.add(content);
        }

        if (contents.size() + skewers.size() == 0) {
            return;
        }

        ItemStack bowl = offhandItemStack.split(1);

        HotpotPaperBowlItem.setPaperBowlSoupType(bowl, hotpotBlockEntity.getSoup());
        HotpotPaperBowlItem.setPaperBowlItems(bowl, contents);
        HotpotPaperBowlItem.setPaperBowlSkewers(bowl, skewers);
        HotpotPaperBowlItem.setPaperBowlSoupStatus(bowl, soupStatus);

        selfPos.playSound(SoundEvents.BOTTLE_FILL, SoundSource.PLAYERS, 1.0F, 1.0F);

        if (!player.addItem(bowl)) {
            selfPos.dropItemStack(bowl);
        }
    }

    public HotpotSoupStatus getSoupStatus() {
        return soupStatus;
    }
}
