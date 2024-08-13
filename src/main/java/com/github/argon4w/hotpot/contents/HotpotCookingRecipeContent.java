package com.github.argon4w.hotpot.contents;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.items.IHotpotSpecialHotpotCookingRecipeItem;
import com.github.argon4w.hotpot.soups.IHotpotSoup;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

import java.util.Optional;

public class HotpotCookingRecipeContent extends AbstractHotpotRecipeContent {
    public static final RecipeManager.CachedCheck<SingleRecipeInput, CampfireCookingRecipe> COOKING_RECIPE_QUICK_CHECK = RecipeManager.createCheck(RecipeType.CAMPFIRE_COOKING);

    public HotpotCookingRecipeContent(ItemStack itemStack, int cookingTime, float cookingProgress, double experience) {
        super(itemStack, cookingTime, cookingProgress, experience);
    }

    public HotpotCookingRecipeContent(ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        super(itemStack, hotpotBlockEntity, pos);
    }

    @Override
    public Optional<AbstractCookingRecipe> getRecipe(ItemStack itemStack, LevelBlockPos pos) {
        return HotpotCookingRecipeContent.COOKING_RECIPE_QUICK_CHECK.getRecipeFor(new SingleRecipeInput(itemStack), pos.level()).map(RecipeHolder::value);
    }

    @Override
    public Optional<Integer> remapCookingTime(IHotpotSoup soupType, ItemStack itemStack, LevelBlockPos pos, HotpotBlockEntity hotpotBlockEntity) {
        return itemStack.getItem() instanceof IHotpotSpecialHotpotCookingRecipeItem item ? Optional.of(item.getCookingTime(soupType, itemStack, pos, hotpotBlockEntity, this)) : super.remapCookingTime(soupType, itemStack, pos, hotpotBlockEntity);
    }

    @Override
    public Optional<Double> remapExperience(IHotpotSoup soupType, ItemStack itemStack, LevelBlockPos pos, HotpotBlockEntity hotpotBlockEntity) {
        return itemStack.getItem() instanceof IHotpotSpecialHotpotCookingRecipeItem item ? Optional.of(item.getExperience(soupType, itemStack, pos, hotpotBlockEntity, this)) : super.remapExperience(soupType, itemStack, pos, hotpotBlockEntity);
    }

    @Override
    public Optional<ItemStack> remapResult(IHotpotSoup soupType, ItemStack itemStack, LevelBlockPos pos, HotpotBlockEntity hotpotBlockEntity) {
        return itemStack.getItem() instanceof IHotpotSpecialHotpotCookingRecipeItem item ? Optional.of(item.getResult(soupType, itemStack, pos, hotpotBlockEntity, this)) : super.remapResult(soupType, itemStack, pos, hotpotBlockEntity);
    }

    @Override
    public Holder<IHotpotContentSerializer<?>> getContentSerializerHolder() {
        return HotpotContentSerializers.COOKING_RECIPE_CONTENT_SERIALIZER;
    }

    public static class Serializer extends AbstractHotpotItemStackContent.Serializer<HotpotCookingRecipeContent> {
        @Override
        public HotpotCookingRecipeContent buildFromData(ItemStack itemStack, int cookingTime, float cookingProgress, double experience) {
            return new HotpotCookingRecipeContent(itemStack, cookingTime, cookingProgress, experience);
        }

        @Override
        public HotpotCookingRecipeContent get(ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
            return new HotpotCookingRecipeContent(itemStack, hotpotBlockEntity, pos);
        }
    }
}
