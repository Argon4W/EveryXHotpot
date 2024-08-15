package com.github.argon4w.hotpot.contents;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.soups.recipes.IHotpotCookingRecipeHolder;
import com.github.argon4w.hotpot.soups.recipes.holder.AbstractCookingRecipeHolder;
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
    public Optional<IHotpotCookingRecipeHolder> getRecipe(ItemStack itemStack, LevelBlockPos pos) {
        return HotpotCookingRecipeContent.COOKING_RECIPE_QUICK_CHECK.getRecipeFor(new SingleRecipeInput(itemStack), pos.level()).map(RecipeHolder::value).map(AbstractCookingRecipeHolder::new);
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
