package com.github.argon4w.hotpot.contents;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.soups.IHotpotSoup;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

import java.util.Optional;

public class HotpotSmeltingRecipeContent extends AbstractHotpotRecipeContent {
    public static final RecipeManager.CachedCheck<SingleRecipeInput, BlastingRecipe> SMELTING_RECIPE_QUICK_CHECK = RecipeManager.createCheck(RecipeType.BLASTING);

    public HotpotSmeltingRecipeContent(ItemStack itemStack, int cookingTime, float cookingProgress, double experience) {
        super(itemStack, cookingTime, cookingProgress, experience);
    }

    public HotpotSmeltingRecipeContent(ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
        super(itemStack, hotpotBlockEntity, pos);
    }

    @Override
    public Optional<AbstractCookingRecipe> getRecipe(ItemStack itemStack, LevelBlockPos pos) {
        return HotpotSmeltingRecipeContent.SMELTING_RECIPE_QUICK_CHECK.getRecipeFor(new SingleRecipeInput(itemStack), pos.level()).map(RecipeHolder::value);
    }

    @Override
    public Optional<Integer> remapCookingTime(IHotpotSoup soupType, ItemStack itemStack, LevelBlockPos pos, HotpotBlockEntity hotpotBlockEntity) {
        return super.remapCookingTime(soupType, itemStack, pos, hotpotBlockEntity).map(integer -> (int) (integer * 1.5f));
    }

    public static boolean hasSmeltingRecipe(ItemStack itemStack, LevelBlockPos pos) {
        return HotpotSmeltingRecipeContent.SMELTING_RECIPE_QUICK_CHECK.getRecipeFor(new SingleRecipeInput(itemStack), pos.level()).isPresent();
    }

    @Override
    public Holder<IHotpotContentSerializer<?>> getContentSerializerHolder() {
        return HotpotContentSerializers.SMELTING_RECIPE_CONTENT_SERIALIZER;
    }

    public static class Serializer extends AbstractHotpotItemStackContent.Serializer<HotpotSmeltingRecipeContent> {
        @Override
        public HotpotSmeltingRecipeContent buildFromData(ItemStack itemStack, int cookingTime, float cookingProgress, double experience) {
            return new HotpotSmeltingRecipeContent(itemStack, cookingTime, cookingProgress, experience);
        }

        @Override
        public HotpotSmeltingRecipeContent getFromItem(ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, LevelBlockPos pos) {
            return new HotpotSmeltingRecipeContent(itemStack, hotpotBlockEntity, pos);
        }
    }
}
