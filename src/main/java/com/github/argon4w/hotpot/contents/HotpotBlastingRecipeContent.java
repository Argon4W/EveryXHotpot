package com.github.argon4w.hotpot.contents;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.soups.HotpotComponentSoup;
import com.github.argon4w.hotpot.soups.recipes.IHotpotCookingRecipeHolder;
import com.github.argon4w.hotpot.soups.recipes.holder.BlastingRecipeHolder;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

import java.util.Optional;

public class HotpotBlastingRecipeContent extends AbstractHotpotRecipeContent {
    public static final RecipeManager.CachedCheck<SingleRecipeInput, BlastingRecipe> SMELTING_RECIPE_CHECK = RecipeManager.createCheck(RecipeType.BLASTING);

    public HotpotBlastingRecipeContent(ItemStack itemStack, int cookingTime, double cookingProgress, double experience) {
        super(itemStack, cookingTime, cookingProgress, experience);
    }

    public HotpotBlastingRecipeContent(ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        super(itemStack, hotpotBlockEntity, pos);
    }

    @Override
    public Optional<IHotpotCookingRecipeHolder> getRecipe(HotpotComponentSoup soup, ItemStack itemStack, LevelBlockPos pos) {
        return HotpotBlastingRecipeContent.SMELTING_RECIPE_CHECK.getRecipeFor(new SingleRecipeInput(itemStack), pos.level()).map(RecipeHolder::value).map(BlastingRecipeHolder::new);
    }

    @Override
    public Holder<IHotpotContentSerializer<?>> getContentSerializerHolder() {
        return HotpotContentSerializers.SMELTING_RECIPE_CONTENT_SERIALIZER;
    }

    public static class Serializer extends AbstractHotpotItemStackContent.Serializer<HotpotBlastingRecipeContent> {
        @Override
        public HotpotBlastingRecipeContent getFromData(ItemStack itemStack, int cookingTime, double cookingProgress, double experience) {
            return new HotpotBlastingRecipeContent(itemStack, cookingTime, cookingProgress, experience);
        }

        @Override
        public HotpotBlastingRecipeContent get(ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
            return new HotpotBlastingRecipeContent(itemStack, hotpotBlockEntity, pos);
        }
    }
}
