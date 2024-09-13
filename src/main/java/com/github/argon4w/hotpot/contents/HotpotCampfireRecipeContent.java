package com.github.argon4w.hotpot.contents;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.soups.HotpotComponentSoup;
import com.github.argon4w.hotpot.soups.recipes.IHotpotCookingRecipeHolder;
import com.github.argon4w.hotpot.soups.recipes.holder.AbstractCookingRecipeHolder;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

import java.util.Optional;

public class HotpotCampfireRecipeContent extends AbstractHotpotRecipeContent {
    public static final RecipeManager.CachedCheck<SingleRecipeInput, CampfireCookingRecipe> COOKING_RECIPE_CHECK = RecipeManager.createCheck(RecipeType.CAMPFIRE_COOKING);

    public HotpotCampfireRecipeContent(ItemStack itemStack, ItemStack originalItemStack, int cookingTime, double cookingProgress, double experience) {
        super(itemStack, originalItemStack, cookingTime, cookingProgress, experience);
    }

    public HotpotCampfireRecipeContent(ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        super(itemStack, hotpotBlockEntity, pos);
    }

    @Override
    public Optional<IHotpotCookingRecipeHolder> getRecipe(HotpotComponentSoup soup, ItemStack itemStack, LevelBlockPos pos) {
        return HotpotCampfireRecipeContent.COOKING_RECIPE_CHECK.getRecipeFor(new SingleRecipeInput(itemStack), pos.level()).map(RecipeHolder::value).map(AbstractCookingRecipeHolder::new);
    }

    @Override
    public Holder<IHotpotContentSerializer<?>> getContentSerializerHolder() {
        return HotpotContentSerializers.CAMPFIRE_RECIPE_CONTENT_SERIALIZER;
    }

    public static class Serializer extends AbstractHotpotItemStackContent.Serializer<HotpotCampfireRecipeContent> {
        @Override
        public HotpotCampfireRecipeContent getFromData(ItemStack itemStack, ItemStack originalItemStack, int cookingTime, double cookingProgress, double experience) {
            return new HotpotCampfireRecipeContent(itemStack, originalItemStack, cookingTime, cookingProgress, experience);
        }

        @Override
        public HotpotCampfireRecipeContent get(ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
            return new HotpotCampfireRecipeContent(itemStack, hotpotBlockEntity, pos);
        }
    }
}
