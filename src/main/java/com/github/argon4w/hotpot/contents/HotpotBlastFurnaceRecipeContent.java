package com.github.argon4w.hotpot.contents;

import com.github.argon4w.hotpot.BlockPosWithLevel;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;

import java.util.Optional;

public class HotpotCampfireRecipeContent extends AbstractHotpotItemStackContent {
    public HotpotCampfireRecipeContent(ItemStack itemStack) {
        super(itemStack);
    }

    public HotpotCampfireRecipeContent() {
        super();
    }

    public Optional<CampfireCookingRecipe> getRecipe(ItemStack itemStack, BlockPosWithLevel pos) {
        return HotpotContents.CAMPFIRE_QUICK_CHECK.getRecipeFor(new SimpleContainer(itemStack), pos.level());
    }

    @Override
    public int remapCookingTime(ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {
        return getRecipe(itemStack, pos).map(AbstractCookingRecipe::getCookingTime).orElse(-1);
    }

    @Override
    public Optional<Float> remapExperience(ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {
        return getRecipe(itemStack, pos).map(AbstractCookingRecipe::getExperience);
    }

    @Override
    public Optional<ItemStack> remapResult(ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {
        return getRecipe(itemStack, pos).map(recipe -> recipe.assemble(new SimpleContainer(itemStack), pos.level().registryAccess()));
    }
}
