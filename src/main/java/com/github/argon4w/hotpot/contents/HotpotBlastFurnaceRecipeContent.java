package com.github.argon4w.hotpot.contents;

import com.github.argon4w.hotpot.BlockPosWithLevel;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.BlastingRecipe;

import java.util.Optional;

public class HotpotBlastFurnaceRecipeContent extends AbstractHotpotCookingRecipeContent {
    public HotpotBlastFurnaceRecipeContent(ItemStack itemStack) {
        super(itemStack);
    }

    public HotpotBlastFurnaceRecipeContent() {
        super();
    }

    @Override
    public Optional<BlastingRecipe> getRecipe(ItemStack itemStack, BlockPosWithLevel pos) {
        return pos.level().getRecipeManager().getRecipeFor(HotpotContents.BlAST_FURNACE_COOKING_RECIPE, new Inventory(itemStack), pos.level());
    }

    @Override
    public int remapCookingTime(ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {
        return (int) (super.remapCookingTime(itemStack, hotpotBlockEntity, pos) * 1.5f);
    }

    @Override
    public String getID() {
        return "BlastingItemStack";
    }

    public static boolean hasBlastingRecipe(ItemStack itemStack, BlockPosWithLevel pos) {
        return pos.level().getRecipeManager().getRecipeFor(HotpotContents.BlAST_FURNACE_COOKING_RECIPE, new Inventory(itemStack), pos.level()).isPresent();
    }
}
