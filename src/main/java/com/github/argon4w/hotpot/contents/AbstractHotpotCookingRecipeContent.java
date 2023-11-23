package com.github.argon4w.hotpot.contents;

import com.github.argon4w.hotpot.BlockPosWithLevel;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;

import java.util.Optional;

public abstract class AbstractHotpotCookingRecipeContent  extends AbstractHotpotItemStackContent {
    public AbstractHotpotCookingRecipeContent(ItemStack itemStack) {
        super(itemStack);
    }

    public AbstractHotpotCookingRecipeContent() {
        super();
    }

    public abstract Optional<? extends AbstractCookingRecipe> getRecipe(ItemStack itemStack, BlockPosWithLevel pos);

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

    @Override
    public String getID() {
        return "BlastingItemStack";
    }

    public static boolean hasBlastingRecipe(ItemStack itemStack, BlockPosWithLevel pos) {
        return HotpotContents.BLAST_FURNACE_QUICK_CHECK.getRecipeFor(new SimpleContainer(itemStack), pos.level()).isPresent();
    }
}
