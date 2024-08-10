package com.github.argon4w.hotpot.soups.types;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.contents.IHotpotContent;
import com.github.argon4w.hotpot.contents.IHotpotContentSerializer;
import com.github.argon4w.hotpot.soups.HotpotSoupTypeSerializers;
import com.github.argon4w.hotpot.soups.IHotpotSoup;
import com.github.argon4w.hotpot.soups.synchronizers.HotpotSoupDiscardOverflowSynchronizer;
import com.github.argon4w.hotpot.soups.synchronizers.HotpotSoupTickSynchronizer;
import com.github.argon4w.hotpot.soups.synchronizers.HotpotSoupWaterLevelSynchronizer;
import com.github.argon4w.hotpot.soups.synchronizers.IHotpotSoupSynchronizer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

public abstract class AbstractHotpotSoup implements IHotpotSoup {
    protected float waterLevel = 1f;
    protected float overflowWaterLevel = 0f;

    public abstract boolean canItemEnter(ItemEntity itemEntity);

    @Override
    public Optional<IHotpotContentSerializer<?>> interact(int hitPos, Player player, InteractionHand hand, ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos selfPos) {
        if (!itemStack.isEmpty()) {
            return getContentSerializerFromItemStack(itemStack, hotpotBlockEntity, selfPos);
        }

        if (player.isCrouching() && hotpotBlockEntity.canBeRemoved()) {
            hotpotBlockEntity.setSoup(HotpotSoupTypeSerializers.buildEmptySoup(), selfPos);
            hotpotBlockEntity.onRemove(selfPos);

            return Optional.empty();
        }

        player.hurt(player.damageSources().onFire(), 5);
        hotpotBlockEntity.getContentViaHand(player, hitPos, selfPos);

        return Optional.empty();
    }

    @Override
    public void onContentUpdate(IHotpotContent content, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        hotpotBlockEntity.getContents().stream().filter(content1 -> content1 != content).forEach(content1 -> content1.onOtherContentUpdate(content, hotpotBlockEntity, pos));
    }

    @Override
    public ItemStack getContentViaTableware(IHotpotContent content, ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        return itemStack;
    }

    @Override
    public void getContentViaHand(IHotpotContent content, ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        pos.dropItemStack(itemStack);
    }

    @Override
    public void setWaterLevel(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos, float waterLevel) {
        setWaterLevel(waterLevel);
    }

    @Override
    public void onEntityInside(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos selfPos, Entity entity) {
        if (entity instanceof ItemEntity itemEntity) {
            if (!canItemEnter(itemEntity)) {
                return;
            }

            ItemStack stack = itemEntity.getItem();

            if (stack.isEmpty()) {
                return;
            }

            hotpotBlockEntity.tryPlaceItemStack(HotpotBlockEntity.getHitPos(selfPos.pos(), itemEntity.position()), stack, selfPos);
            itemEntity.setItem(stack);

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
    public float getWaterLevel() {
        return waterLevel;
    }

    @Override
    public float getOverflowWaterLevel() {
        return overflowWaterLevel;
    }

    @Override
    public void discardOverflowWaterLevel(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        overflowWaterLevel = 0f;
    }

    public void setWaterLevel(float waterLevel) {
        this.waterLevel = Math.clamp(waterLevel, 0.0f, 1.0f);
        this.overflowWaterLevel = Math.max(0.0f, waterLevel - 1f);
    }
}
