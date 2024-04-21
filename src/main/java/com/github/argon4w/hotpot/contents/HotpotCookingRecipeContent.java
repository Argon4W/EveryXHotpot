package com.github.argon4w.hotpot.contents;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.items.IHotpotSpecialHotpotCookingRecipeItem;
import com.github.argon4w.hotpot.soups.IHotpotSoupType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.Optional;

public class HotpotCookingRecipeContent extends AbstractHotpotRecipeContent {
    public static final RecipeManager.CachedCheck<Container, CampfireCookingRecipe> COOKING_RECIPE_QUICK_CHECK = RecipeManager.createCheck(RecipeType.CAMPFIRE_COOKING);

    public HotpotCookingRecipeContent(ItemStack itemStack) {
        super(itemStack);
    }

    public HotpotCookingRecipeContent() {
        super();
    }

    @Override
    public Optional<? extends AbstractCookingRecipe> getRecipe(ItemStack itemStack, LevelBlockPos pos) {
        return HotpotCookingRecipeContent.COOKING_RECIPE_QUICK_CHECK.getRecipeFor(new SimpleContainer(itemStack), pos.level());
    }

    @Override
    public Optional<Integer> remapCookingTime(IHotpotSoupType soupType, ItemStack itemStack, LevelBlockPos pos) {
        if (itemStack.getItem() instanceof IHotpotSpecialHotpotCookingRecipeItem item) {
            return Optional.of(item.getCookingTime(itemStack, soupType, pos));
        }

        return super.remapCookingTime(soupType, itemStack, pos);
    }

    @Override
    public Optional<Float> remapExperience(IHotpotSoupType soupType, ItemStack itemStack, LevelBlockPos pos) {
        if (itemStack.getItem() instanceof IHotpotSpecialHotpotCookingRecipeItem item) {
            return Optional.of(item.getExperience(itemStack, soupType, pos));
        }

        return super.remapExperience(soupType, itemStack, pos);
    }

    @Override
    public Optional<ItemStack> remapResult(IHotpotSoupType soupType, ItemStack itemStack, LevelBlockPos pos) {
        if (itemStack.getItem() instanceof IHotpotSpecialHotpotCookingRecipeItem item) {
            return Optional.of(item.getResult(itemStack, soupType, pos));
        }

        return super.remapResult(soupType, itemStack, pos);
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return new ResourceLocation(HotpotModEntry.MODID, "cooking_recipe_content");
    }
}
