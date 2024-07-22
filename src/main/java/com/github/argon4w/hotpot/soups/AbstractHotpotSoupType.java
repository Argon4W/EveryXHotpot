package com.github.argon4w.hotpot.soups;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.contents.IHotpotContent;
import com.github.argon4w.hotpot.soups.synchronizers.HotpotSoupDiscardOverflowSynchronizer;
import com.github.argon4w.hotpot.soups.synchronizers.HotpotSoupTickSynchronizer;
import com.github.argon4w.hotpot.soups.synchronizers.HotpotSoupWaterLevelSynchronizer;
import com.github.argon4w.hotpot.soups.synchronizers.IHotpotSoupSynchronizer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

public abstract class AbstractHotpotSoupType implements IHotpotSoupType {
    private float waterLevel = 1f;
    private float overflowWaterLevel = 0f;

    public abstract boolean canItemEnter(ItemEntity itemEntity);

    @Override
    public IHotpotSoupType load(CompoundTag compoundTag) {
        setWaterLevel(compoundTag.getFloat("WaterLevel"));

        return this;
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag) {
        compoundTag.putFloat("WaterLevel", getWaterLevel());

        return compoundTag;
    }

    @Override
    public boolean isValid(CompoundTag compoundTag) {
        return compoundTag.contains("WaterLevel", Tag.TAG_FLOAT);
    }

    @Override
    public Optional<IHotpotContent> interact(int hitPos, Player player, InteractionHand hand, ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos selfPos) {
        if (itemStack.isEmpty()) {
            if (player.isCrouching() && hotpotBlockEntity.canBeRemoved()) {
                hotpotBlockEntity.setSoup(HotpotModEntry.HOTPOT_SOUP_FACTORY_MANAGER.buildEmptySoup(), selfPos);
                hotpotBlockEntity.onRemove(selfPos);
            } else {
                player.hurt(player.damageSources().onFire(), 5);
                hotpotBlockEntity.tryTakeOutContentViaHand(player, hitPos, selfPos);
            }

            return Optional.empty();
        }

        return remapItemStack(player.getAbilities().instabuild, itemStack, selfPos);
    }

    @Override
    public Optional<IHotpotContent> remapContent(IHotpotContent content, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        content.create(hotpotBlockEntity, pos);

        return Optional.of(content);
    }

    @Override
    public void contentUpdate(IHotpotContent content, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        hotpotBlockEntity.getContents().stream()
                .filter(content1 -> content1 != content)
                .forEach(content1 -> content1.onOtherContentUpdate(content, hotpotBlockEntity, pos));
    }

    @Override
    public ItemStack takeOutContentViaTableware(IHotpotContent content, ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        return itemStack;
    }

    @Override
    public void takeOutContentViaHand(IHotpotContent content, ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        pos.dropItemStack(itemStack);
    }

    @Override
    public void setWaterLevel(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos, float waterLevel) {
        setWaterLevel(waterLevel);
    }

    @Override
    public void entityInside(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos selfPos, Entity entity) {
        if (entity instanceof ItemEntity itemEntity) {
            if (!canItemEnter(itemEntity)) {
                return;
            }

            ItemStack stack = itemEntity.getItem();

            if (!stack.isEmpty()) {
                remapItemStack(false, stack, selfPos).ifPresent(content -> hotpotBlockEntity.tryPlaceContent(HotpotBlockEntity.getHitPos(selfPos.pos(), itemEntity.position()), content, selfPos));
                itemEntity.setItem(stack);
            }

            return;
        }

        if (entity.isAttackable()) {
            entity.hurt(new DamageSource(HotpotModEntry.IN_HOTPOT_DAMAGE_TYPE.apply(selfPos.level()), selfPos.toVec3()), 3f);
        }
    }

    @Override
    public List<IHotpotSoupSynchronizer> getSynchronizer(HotpotBlockEntity selfHotpotBlockEntity, LevelBlockPos selfPos) {
        return List.of(new HotpotSoupTickSynchronizer(), new HotpotSoupWaterLevelSynchronizer(), new HotpotSoupDiscardOverflowSynchronizer());
    }

    @Override
    public float getWaterLevel(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        return waterLevel;
    }

    @Override
    public float getOverflowWaterLevel(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        return overflowWaterLevel;
    }

    @Override
    public void discardOverflowWaterLevel(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
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
