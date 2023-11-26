package com.github.argon4w.hotpot.contents;

import com.github.argon4w.hotpot.BlockPosWithLevel;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.BlastingRecipe;

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
        return HotpotContents.BLAST_FURNACE_QUICK_CHECK.getRecipeFor(new SimpleContainer(itemStack), pos.level());
    }

    @Override
    public int remapCookingTime(ItemStack itemStack, HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos) {
        return (int) (super.remapCookingTime(itemStack, hotpotBlockEntity, pos) * 1.5f);
    }

    @Override
    public String getID() {
        return "blasting_recipe_content";
    }

    public static boolean hasBlastingRecipe(ItemStack itemStack, BlockPosWithLevel pos) {
        return HotpotContents.BLAST_FURNACE_QUICK_CHECK.getRecipeFor(new SimpleContainer(itemStack), pos.level()).isPresent();
    }
}
