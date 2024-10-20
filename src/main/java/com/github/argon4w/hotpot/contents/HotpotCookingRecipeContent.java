package com.github.argon4w.hotpot.contents;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.api.contents.IHotpotContentSerializer;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.soups.HotpotComponentSoup;
import com.github.argon4w.hotpot.soups.recipes.HotpotSoupCookingRecipe;
import com.github.argon4w.hotpot.api.soups.recipes.IHotpotCookingRecipeHolder;
import com.github.argon4w.hotpot.soups.recipes.holder.HotpotCookingRecipeHolder;
import com.github.argon4w.hotpot.soups.recipes.input.HotpotRecipeInput;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.Optional;

public class HotpotCookingRecipeContent extends AbstractHotpotRecipeContent {
    public static final RecipeManager.CachedCheck<HotpotRecipeInput, HotpotSoupCookingRecipe> HOTPOT_COOKING_RECIPE_CHECK = RecipeManager.createCheck(HotpotModEntry.HOTPOT_SOUP_COOKING_RECIPE_TYPE.get());

    public HotpotCookingRecipeContent(ItemStack itemStack, ItemStack originalItemStack, int cookingTime, double cookingProgress, double experience) {
        super(itemStack, originalItemStack, cookingTime, cookingProgress, experience);
    }

    public HotpotCookingRecipeContent(ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        super(itemStack, hotpotBlockEntity, pos);
    }

    @Override
    public Optional<IHotpotCookingRecipeHolder> getRecipe(HotpotComponentSoup soup, ItemStack itemStack, LevelBlockPos pos) {
        return getHotpotCookingRecipe(soup, itemStack, pos).map(RecipeHolder::value).map(HotpotCookingRecipeHolder::new);
    }

    @Override
    public Holder<IHotpotContentSerializer<?>> getContentSerializerHolder() {
        return HotpotContentSerializers.COOKING_RECIPE_CONTENT_SERIALIZER;
    }

    public static boolean hasRecipe(HotpotComponentSoup soup, ItemStack itemStack, LevelBlockPos pos) {
        return getHotpotCookingRecipe(soup, itemStack, pos).isPresent();
    }

    public static Optional<RecipeHolder<HotpotSoupCookingRecipe>> getHotpotCookingRecipe(HotpotComponentSoup soup, ItemStack itemStack, LevelBlockPos pos) {
        return HOTPOT_COOKING_RECIPE_CHECK.getRecipeFor(new HotpotRecipeInput(itemStack, soup), pos.level());
    }

    public static class Serializer extends AbstractHotpotRecipeContent.Serializer<HotpotCookingRecipeContent> {
        @Override
        public HotpotCookingRecipeContent createContent(ItemStack itemStack, ItemStack originalItemStack, int cookingTime, double cookingProgress, double experience) {
            return new HotpotCookingRecipeContent(itemStack, originalItemStack, cookingTime, cookingProgress, experience);
        }

        @Override
        public HotpotCookingRecipeContent createContent(ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos, Direction direction) {
            return new HotpotCookingRecipeContent(itemStack, hotpotBlockEntity, pos);
        }
    }
}
