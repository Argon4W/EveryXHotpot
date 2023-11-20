package com.github.argon4w.hotpot.contents;

import com.github.argon4w.hotpot.BlockPosWithLevel;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CampfireCookingRecipe;

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
        return pos.level().getRecipeManager().getRecipeFor(HotpotContents.CAMPFIRE_COOKING_RECIPE, new Inventory(itemStack), pos.level());
    }

    @Override
    public String getID() {
        return "ItemStack";
    }
}
