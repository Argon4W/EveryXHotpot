package com.github.argon4w.hotpot.contents;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.soups.IHotpotSoupType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.Optional;

public class HotpotSmeltingRecipeContent extends AbstractHotpotRecipeContent {
    public static final RecipeManager.CachedCheck<Container, BlastingRecipe> SMELTING_RECIPE_QUICK_CHECK = RecipeManager.createCheck(RecipeType.BLASTING);

    public HotpotSmeltingRecipeContent(ItemStack itemStack) {
        super(itemStack);
    }

    public HotpotSmeltingRecipeContent() {
        super();
    }

    @Override
    public Optional<BlastingRecipe> getRecipe(ItemStack itemStack, LevelBlockPos pos) {
        return HotpotSmeltingRecipeContent.SMELTING_RECIPE_QUICK_CHECK.getRecipeFor(new SimpleContainer(itemStack), pos.level());
    }

    @Override
    public Optional<Integer> remapCookingTime(IHotpotSoupType soupType, ItemStack itemStack, LevelBlockPos pos) {
        return super.remapCookingTime(soupType, itemStack, pos).map(integer -> (int) (integer * 1.5f));
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return new ResourceLocation(HotpotModEntry.MODID, "smelting_recipe_content");
    }

    public static boolean hasSmeltingRecipe(ItemStack itemStack, LevelBlockPos pos) {
        return HotpotSmeltingRecipeContent.SMELTING_RECIPE_QUICK_CHECK.getRecipeFor(new SimpleContainer(itemStack), pos.level()).isPresent();
    }
}
