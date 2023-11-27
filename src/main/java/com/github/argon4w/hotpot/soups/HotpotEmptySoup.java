package com.github.argon4w.hotpot.soups;

import com.github.argon4w.hotpot.BlockPosWithLevel;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.contents.HotpotContents;
import com.github.argon4w.hotpot.contents.IHotpotContent;
import com.github.argon4w.hotpot.soups.renderers.IHotpotSoupCustomElementRenderer;
import com.github.argon4w.hotpot.soups.synchronizers.IHotpotSoupSynchronizer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class HotpotEmptySoup implements IHotpotSoup {
    public static final ConcurrentHashMap<Predicate<ItemStack>, HotpotInteraction> HOTPOT_INTERACTION_RECIPES = new ConcurrentHashMap<>(Map.of(
            (itemStack) -> itemStack.is(Items.WATER_BUCKET), new HotpotInteraction(HotpotSoups.CLEAR_SOUP.get(), 1f, SoundEvents.BUCKET_EMPTY, () -> new ItemStack(Items.BUCKET)),
            (itemStack) -> itemStack.is(Items.POTION) && PotionUtils.getPotion(itemStack) == Potions.WATER, new HotpotInteraction(HotpotSoups.CLEAR_SOUP.get(), 0.333f, SoundEvents.BOTTLE_FILL, () -> new ItemStack(Items.GLASS_BOTTLE)),
            (itemStack) -> itemStack.is(Items.MILK_BUCKET), new HotpotInteraction(HotpotSoups.CHEESE_SOUP.get(), 1f, SoundEvents.BUCKET_EMPTY, () -> new ItemStack(Items.BUCKET)),
            (itemStack) -> itemStack.is(Items.LAVA_BUCKET), new HotpotInteraction(HotpotSoups.LAVA_SOUP.get(), 1f, SoundEvents.BUCKET_EMPTY_LAVA, () -> new ItemStack(Items.BUCKET))
    ));

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
        return "empty_soup";
    }

    @Override
    public Optional<IHotpotContent> interact(int hitSection, Player player, InteractionHand hand, ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel selfPos) {
        lookupInteractionRecipe(itemStack, interaction -> {
            player.setItemInHand(hand, ItemUtils.createFilledResult(itemStack, player, interaction.returned().get()));

            hotpotBlockEntity.setSoup(interaction.soup().createSoup(), selfPos);
            hotpotBlockEntity.getSoup().setWaterLevel(hotpotBlockEntity, selfPos, interaction.waterLevel());

            selfPos.level().playSound(null, selfPos.pos(), interaction.soundEvent(), SoundSource.BLOCKS, 1.0F, 1.0F);
        });

        return Optional.empty();
    }

    @Override
    public Optional<IHotpotContent> remapContent(IHotpotContent content, HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {
        return Optional.of(HotpotContents.getEmptyContent().createContent());
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

    @Override
    public List<IHotpotSoupCustomElementRenderer> getCustomElementRenderers() {
        return List.of();
    }

    public static void lookupInteractionRecipe(ItemStack itemStack, Consumer<HotpotInteraction> consumer) {
        Optional<Predicate<ItemStack>> key = HotpotEmptySoup.HOTPOT_INTERACTION_RECIPES.keySet().stream().filter(predicate -> predicate.test(itemStack)).findFirst();
        key.ifPresent(itemStackPredicate -> consumer.accept(HotpotEmptySoup.HOTPOT_INTERACTION_RECIPES.get(itemStackPredicate)));
    }

    public record HotpotInteraction(HotpotSoupType<?> soup, float waterLevel, SoundEvent soundEvent, Supplier<ItemStack> returned) {}
}
