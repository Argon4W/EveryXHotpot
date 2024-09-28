package com.github.argon4w.hotpot.contents;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.api.contents.IHotpotContentSerializer;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.soups.HotpotComponentSoup;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.List;
import java.util.Optional;

public class HotpotPiglinBarterRecipeContent extends AbstractHotpotItemStackContent {
    public HotpotPiglinBarterRecipeContent(ItemStack itemStack, ItemStack originalItemStack, int cookingTime, double cookingProgress, double experience) {
        super(itemStack, originalItemStack, cookingTime, cookingProgress, experience);
    }

    public HotpotPiglinBarterRecipeContent(ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        super(itemStack, hotpotBlockEntity, pos);
    }

    @Override
    public boolean shouldRemove(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        return getCookingTime() < 0 || itemStack.isEmpty();
    }

    @Override
    public Optional<Integer> getCookingTime(HotpotComponentSoup soup, ItemStack itemStack, LevelBlockPos pos, HotpotBlockEntity hotpotBlockEntity) {
        return hasBarterResponseItemStacks() ? Optional.of(300) : Optional.empty();
    }

    @Override
    public Optional<Double> getExperience(HotpotComponentSoup soup, ItemStack itemStack, LevelBlockPos pos, HotpotBlockEntity hotpotBlockEntity) {
        return Optional.empty();
    }

    @Override
    public Optional<ItemStack> getResult(HotpotComponentSoup soup, ItemStack itemStack, LevelBlockPos pos, HotpotBlockEntity hotpotBlockEntity) {
        return hasBarterResponseItemStacks() ? Optional.of(ItemStack.EMPTY) : Optional.empty();
    }

    @Override
    public Holder<IHotpotContentSerializer<?>> getContentSerializerHolder() {
        return HotpotContentSerializers.PIGLIN_BARTER_RECIPE_CONTENT_SERIALIZER;
    }

    public boolean hasBarterResponseItemStacks() {
        return originalItemStack.is(Items.GOLD_INGOT);
    }

    @Override
    public List<ItemStack> getContentResultItemStacks(HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        return cookingTime > 0 ? List.of(originalItemStack) : pos.level() instanceof ServerLevel serverLevel && hasBarterResponseItemStacks() ? pos.getLootTable(BuiltInLootTables.PIGLIN_BARTERING).getRandomItems(new LootParams.Builder(serverLevel).withParameter(LootContextParams.ORIGIN, pos.toVec3()).withParameter(LootContextParams.BLOCK_ENTITY, hotpotBlockEntity).create(LootContextParamSets.EMPTY)) : List.of(originalItemStack);
    }

    public static class Serializer extends AbstractHotpotItemStackContent.Serializer<HotpotPiglinBarterRecipeContent> {
        @Override
        public HotpotPiglinBarterRecipeContent getFromData(ItemStack itemStack, ItemStack originalItemStack, int cookingTime, double cookingProgress, double experience) {
            return new HotpotPiglinBarterRecipeContent(itemStack, originalItemStack, cookingTime, cookingProgress, experience);
        }

        @Override
        public HotpotPiglinBarterRecipeContent get(ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
            return new HotpotPiglinBarterRecipeContent(itemStack, hotpotBlockEntity, pos);
        }
    }
}
