package com.github.argon4w.hotpot.soups;

import com.github.argon4w.hotpot.BlockPosWithLevel;
import com.github.argon4w.hotpot.HotpotDefinitions;
import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.contents.HotpotItemStackContent;
import com.github.argon4w.hotpot.contents.IHotpotContent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public abstract class AbstractHotpotSoup implements IHotpotSoup {
    private float waterLevel = 1f;
    private float overflowWaterLevel = 0f;

    @Override
    public IHotpotSoup load(CompoundTag compoundTag) {
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
    public Optional<IHotpotContent> interact(int hitSection, Player player, InteractionHand hand, ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel selfPos) {
        if (itemStack.isEmpty()) {
            if (player.isCrouching()) {
                hotpotBlockEntity.setSoup(HotpotDefinitions.getEmptySoup().get(), selfPos);
                hotpotBlockEntity.onRemove(selfPos);
            } else {
                player.hurt(player.damageSources().onFire(), 5);
                hotpotBlockEntity.tryTakeOutContentViaHand(hitSection, selfPos);
            }

            return Optional.empty();
        }

        return Optional.of(new HotpotItemStackContent((player.getAbilities().instabuild ? itemStack.copy() : itemStack)));
    }

    @Override
    public Optional<IHotpotContent> remapContent(IHotpotContent content, HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {
        content.placed(hotpotBlockEntity, pos);

        return Optional.of(content);
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
        if (entity instanceof ItemEntity itemEntity) {
            ItemStack stack = itemEntity.getItem();

            if (!stack.isEmpty()) {
                hotpotBlockEntity.tryPlaceContent(HotpotBlockEntity.getPosSection(selfPos.pos(), itemEntity.position()), new HotpotItemStackContent(stack), selfPos);
                itemEntity.setItem(stack);
            }

            return;
        }

        if (entity.isAttackable()) {
            entity.hurt(new DamageSource(HotpotModEntry.IN_HOTPOT_DAMAGE_TYPE.apply(selfPos.level()), selfPos.toVec3()), 3f);
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
