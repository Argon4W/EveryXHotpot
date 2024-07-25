package com.github.argon4w.hotpot.contents;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.items.IHotpotSpecialHotpotCookingRecipeItem;
import com.github.argon4w.hotpot.soups.IHotpotSoupType;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

import java.util.Optional;

public class HotpotCookingRecipeContent extends AbstractHotpotRecipeContent {
    public static final RecipeManager.CachedCheck<SingleRecipeInput, CampfireCookingRecipe> COOKING_RECIPE_QUICK_CHECK = RecipeManager.createCheck(RecipeType.CAMPFIRE_COOKING);

    public HotpotCookingRecipeContent(ItemStack itemStack, int cookingTime, int cookingProgress, float experience) {
        super(itemStack, cookingTime, cookingProgress, experience);
    }

    public HotpotCookingRecipeContent(ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity) {
        super(itemStack, hotpotBlockEntity);
    }

    @Override
    public Optional<AbstractCookingRecipe> getRecipe(ItemStack itemStack, LevelBlockPos pos) {
        return HotpotCookingRecipeContent.COOKING_RECIPE_QUICK_CHECK.getRecipeFor(new SingleRecipeInput(itemStack), pos.level()).map(RecipeHolder::value);
    }

    @Override
    public Optional<Integer> remapCookingTime(IHotpotSoupType soupType, ItemStack itemStack, LevelBlockPos pos, HotpotBlockEntity hotpotBlockEntity) {
        return itemStack.getItem() instanceof IHotpotSpecialHotpotCookingRecipeItem item ? Optional.of(item.getCookingTime(itemStack, soupType, pos, hotpotBlockEntity)) : super.remapCookingTime(soupType, itemStack, pos, hotpotBlockEntity);
    }

    @Override
    public Optional<Float> remapExperience(IHotpotSoupType soupType, ItemStack itemStack, LevelBlockPos pos, HotpotBlockEntity hotpotBlockEntity) {
        return itemStack.getItem() instanceof IHotpotSpecialHotpotCookingRecipeItem item ? Optional.of(item.getExperience(itemStack, soupType, pos, hotpotBlockEntity)) : super.remapExperience(soupType, itemStack, pos, hotpotBlockEntity);
    }

    @Override
    public Optional<ItemStack> remapResult(IHotpotSoupType soupType, ItemStack itemStack, LevelBlockPos pos, HotpotBlockEntity hotpotBlockEntity) {
        return itemStack.getItem() instanceof IHotpotSpecialHotpotCookingRecipeItem item ? Optional.of(item.getResult(itemStack, soupType, pos, hotpotBlockEntity)) : super.remapResult(soupType, itemStack, pos, hotpotBlockEntity);
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return ResourceLocation.fromNamespaceAndPath(HotpotModEntry.MODID, "cooking_recipe_content");
    }

    @Override
    public IHotpotContentFactory<?> getFactory() {
        return HotpotContents.COOKING_RECIPE_CONTENT.get();
    }

    public static class Factory extends AbstractHotpotRecipeContent.Factory<HotpotCookingRecipeContent> {
        @Override
        public HotpotCookingRecipeContent buildFromData(ItemStack itemStack, int cookingTime, int cookingProgress, float experience) {
            return new HotpotCookingRecipeContent(itemStack, cookingTime, cookingProgress, experience);
        }

        @Override
        public HotpotCookingRecipeContent buildFromItem(ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity) {
            return new HotpotCookingRecipeContent(itemStack, hotpotBlockEntity);
        }
    }
}
