package com.github.argon4w.hotpot.contents;

import com.github.argon4w.hotpot.BlockPosWithLevel;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;

import java.util.Optional;

public class HotpotCampfireRecipeContent extends AbstractHotpotCookingRecipeContent {
    public HotpotCampfireRecipeContent(ItemStack itemStack) {
        super(itemStack);
    }

    public HotpotCampfireRecipeContent() {
        super();
    }

    @Override
    public Optional<CampfireCookingRecipe> getRecipe(ItemStack itemStack, BlockPosWithLevel pos) {
        return pos.level().getRecipeManager().getRecipeFor(HotpotContents.CAMPFIRE_COOKING_RECIPE, new SimpleContainer(itemStack), pos.level());
    }

    @Override
    public String getID() {
        return "ItemStack";
    }
}
