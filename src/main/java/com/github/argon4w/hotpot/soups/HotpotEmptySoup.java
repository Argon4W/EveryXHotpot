package com.github.argon4w.hotpot.soups;

import com.github.argon4w.hotpot.BlockPosWithLevel;
import com.github.argon4w.hotpot.HotpotDefinitions;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.contents.IHotpotContent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;

import java.util.Optional;

public class HotpotEmptySoup implements IHotpotSoup {
    @Override
    public IHotpotSoup load(CompoundTag compoundTag) {
        return this;
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag) {
        return compoundTag;
    }

    @Override
    public boolean isValid(CompoundTag compoundTag) {
        return true;
    }

    @Override
    public String getID() {
        return "Empty";
    }

    @Override
    public Optional<IHotpotContent> interact(int hitSection, Player player, InteractionHand hand, ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel selfPos) {
        HotpotDefinitions.ifMatchEmptyFill(itemStack, returnable -> {
            player.setItemInHand(hand, ItemUtils.createFilledResult(itemStack, player, returnable.returned().get()));

            hotpotBlockEntity.setSoup(returnable.soup().get(), selfPos);
            hotpotBlockEntity.getSoup().setWaterLevel(hotpotBlockEntity, selfPos, returnable.waterLevel());

            selfPos.level().playSound(null, selfPos.pos(), returnable.soundEvent(), SoundSource.BLOCKS, 1.0F, 1.0F);
        });

        return Optional.empty();
    }

    @Override
    public Optional<IHotpotContent> remapContent(IHotpotContent content, HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {
        return Optional.of(HotpotDefinitions.getEmptyContent().get());
    }

    @Override
    public Optional<IHotpotSoupSynchronizer> getSynchronizer(HotpotBlockEntity selfHotpotBlockEntity, BlockPosWithLevel selfPos) {
        return Optional.empty();
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
    public void contentUpdate(IHotpotContent content, HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {

    }

    @Override
    public void animateTick(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos, RandomSource randomSource) {

    }

    @Override
    public float getWaterLevel(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {
        return 0;
    }

    @Override
    public float getOverflowWaterLevel(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {
        return 0f;
    }

    @Override
    public void setWaterLevel(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos, float waterLevel) {

    }

    @Override
    public void discardOverflowWaterLevel(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {

    }

    @Override
    public int getContentTickSpeed(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {
        return 0;
    }

    @Override
    public void entityInside(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos, Entity entity) {

    }

    @Override
    public void tick(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {

    }

    @Override
    public Optional<ResourceLocation> getBubbleResourceLocation() {
        return Optional.empty();
    }

    @Override
    public Optional<ResourceLocation> getSoupResourceLocation() {
        return Optional.empty();
    }
}
