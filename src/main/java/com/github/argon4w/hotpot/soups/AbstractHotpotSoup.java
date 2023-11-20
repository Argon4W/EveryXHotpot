package com.github.argon4w.hotpot.soups;

import com.github.argon4w.hotpot.BlockPosWithLevel;
import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.contents.IHotpotContent;
import com.github.argon4w.hotpot.soups.synchronizers.HotpotSoupWaterLevelSynchronizer;
import com.github.argon4w.hotpot.soups.synchronizers.IHotpotSoupSynchronizer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraftforge.common.util.Constants;

import java.util.Optional;

public abstract class AbstractHotpotSoup implements IHotpotSoup {
    private float waterLevel = 1f;
    private float overflowWaterLevel = 0f;

    @Override
    public IHotpotSoup load(CompoundNBT compoundTag) {
        setWaterLevel(compoundTag.getFloat("WaterLevel"));

        return this;
    }

    @Override
    public CompoundNBT save(CompoundNBT compoundTag) {
        compoundTag.putFloat("WaterLevel", getWaterLevel());

        return compoundTag;
    }

    @Override
    public boolean isValid(CompoundNBT compoundTag) {
        return compoundTag.contains("WaterLevel", Constants.NBT.TAG_FLOAT);
    }

    @Override
    public Optional<IHotpotContent> interact(int hitSection, PlayerEntity player, Hand hand, ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel selfPos) {
        if (itemStack.isEmpty()) {
            if (player.isCrouching() && hotpotBlockEntity.canBeRemoved()) {
                hotpotBlockEntity.setSoup(HotpotSoups.getEmptySoup().get(), selfPos);
                hotpotBlockEntity.onRemove(selfPos);
            } else {
                player.hurt(DamageSource.ON_FIRE, 5);
                hotpotBlockEntity.tryTakeOutContentViaHand(hitSection, selfPos);
            }

            return Optional.empty();
        }

        return remapItemStack(player.abilities.instabuild, itemStack, selfPos);
    }

    public abstract Optional<IHotpotContent> remapItemStack(boolean copy, ItemStack itemStack, BlockPosWithLevel pos);

    @Override
    public Optional<IHotpotContent> remapContent(IHotpotContent content, HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {
        content.placed(hotpotBlockEntity, pos);

        return Optional.of(content);
    }

    @Override
    public void contentUpdate(IHotpotContent content, HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {
        hotpotBlockEntity.getContents().stream()
                .filter(content1 -> content1 != content)
                .forEach(content1 -> content1.onOtherContentUpdate(content, hotpotBlockEntity, pos));
    }

    @Override
    public ItemStack takeOutContentViaChopstick(IHotpotContent content, ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {
        return itemStack;
    }

    @Override
    public void takeOutContentViaHand(IHotpotContent content, ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {
        pos.dropItemStack(itemStack);
    }

    @Override
    public void setWaterLevel(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos, float waterLevel) {
        setWaterLevel(waterLevel);
    }

    @Override
    public void entityInside(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel selfPos, Entity entity) {
        if (entity instanceof ItemEntity) {
            ItemEntity itemEntity = (ItemEntity) entity;
            ItemStack stack = itemEntity.getItem();

            if (!stack.isEmpty()) {
                remapItemStack(false, stack, selfPos).ifPresent(content -> hotpotBlockEntity.tryPlaceContent(HotpotBlockEntity.getPosSection(selfPos.pos(), itemEntity.position()), content, selfPos));
                itemEntity.setItem(stack);
            }

            return;
        }

        if (entity.isAttackable()) {
            entity.hurt(HotpotModEntry.HOTPOT_DAMAGE_SOURCE, 3f);
        }
    }

    @Override
    public Optional<IHotpotSoupSynchronizer> getSynchronizer(HotpotBlockEntity selfHotpotBlockEntity, BlockPosWithLevel selfPos) {
        return IHotpotSoupSynchronizer
                .collectOnly((hotpotBlockEntity, pos) -> hotpotBlockEntity.getSoup().tick(hotpotBlockEntity, pos))
                .andThen(new HotpotSoupWaterLevelSynchronizer())
                .andThen((hotpotBlockEntity, pos) -> hotpotBlockEntity.getSoup().discardOverflowWaterLevel(hotpotBlockEntity, pos)).ofOptional();
    }

    @Override
    public float getWaterLevel(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {
        return waterLevel;
    }

    @Override
    public float getOverflowWaterLevel(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {
        return overflowWaterLevel;
    }

    @Override
    public void discardOverflowWaterLevel(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {
        overflowWaterLevel = 0f;
    }

    public void setWaterLevel(float waterLevel) {
        if (waterLevel > 1f) {
            this.waterLevel = 1f;
            overflowWaterLevel = waterLevel - 1f;

            return;
        } else if (waterLevel < 0f) {
            this.waterLevel = 0f;

            return;
        }

        this.waterLevel = waterLevel;
    }

    public float getWaterLevel() {
        return waterLevel;
    }
}
