package com.github.argon4w.hotpot.items;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.HotpotTagsHelper;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.AbstractTablewareInteractiveBlockEntity;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.blocks.HotpotPlacementBlockEntity;
import com.github.argon4w.hotpot.placements.HotpotPlacedSpoon;
import com.github.argon4w.hotpot.placements.HotpotPlacements;
import com.github.argon4w.hotpot.placements.IHotpotPlacement;
import com.github.argon4w.hotpot.soups.HotpotCookingRecipeSoupType;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;

public class HotpotSpoonItem extends HotpotPlacementBlockItem implements IHotpotTablewareItem {
    private final boolean drained;

    public HotpotSpoonItem(boolean drained) {
        super(HotpotPlacements.PLACED_SPOON, new Properties().stacksTo(1));
        this.drained = drained;
    }

    @Override
    public boolean canPlace(Player player, InteractionHand hand, LevelBlockPos pos) {
        return player.isCrouching() || player.isPassenger();
    }

    @Override
    public void fillPlacementData(HotpotPlacementBlockEntity hotpotPlacementBlockEntity, LevelBlockPos pos, IHotpotPlacement placement, ItemStack itemStack) {
        if (placement instanceof HotpotPlacedSpoon placedSpoon) {
            placedSpoon.setSpoonItemSlot(itemStack);
        }
    }

    @Override
    public void tablewareInteract(int hitPos, Player player, InteractionHand hand, ItemStack itemStack, AbstractTablewareInteractiveBlockEntity blockEntity, LevelBlockPos selfPos) {
        if (!(blockEntity instanceof HotpotBlockEntity hotpotBlockEntity)) {
            return;
        }

        if (!(hotpotBlockEntity.getSoup() instanceof HotpotCookingRecipeSoupType soupType)) {
            return;
        }

        if (!(itemStack.getItem() instanceof HotpotSpoonItem hotpotSpoonItem)) {
            return;
        }

        boolean drained = hotpotSpoonItem.isSpoonDrained();
        ItemStack offhandItemStack = player.getItemInHand(InteractionHand.OFF_HAND);

        hotpotBlockEntity.setVelocity(drained ? 20 : 40);

        if (!offhandItemStack.is(HotpotModEntry.HOTPOT_PAPER_BOWL.get())) {
            return;
        }

        if (HotpotTagsHelper.hasHotpotTags(offhandItemStack)) {
            return;
        }

        ArrayList<ItemStack> contents = new ArrayList<>();
        ArrayList<ItemStack> skewers = new ArrayList<>();

        for (int i = 0; i < 8; i ++) {
            ItemStack content = blockEntity.tryTakeOutContentViaTableware(player, i, selfPos);

            if (content.isEmpty()) {
                continue;
            }

            if (content.getItem() instanceof HotpotChopstickItem) {
                selfPos.dropItemStack(content);
                continue;
            }

            if (content.getItem() instanceof HotpotPaperBowlItem) {
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

        HotpotPaperBowlItem.setPaperBowlSoup(bowl, soupType);

        if (contents.size() > 0) {
            HotpotPaperBowlItem.setPaperBowlItems(bowl, contents);
        }

        if (skewers.size() > 0) {
            HotpotPaperBowlItem.setPaperBowlSkewers(bowl, skewers);
        }

        HotpotPaperBowlItem.setPaperBowlDrained(bowl, drained);

        selfPos.level().playSound(null, selfPos.pos(), SoundEvents.BOTTLE_FILL, SoundSource.PLAYERS, 1.0F, 1.0F);

        if (!player.addItem(bowl)) {
            selfPos.dropItemStack(bowl);
        }
    }

    public boolean isSpoonDrained() {
        return drained;
    }
}
