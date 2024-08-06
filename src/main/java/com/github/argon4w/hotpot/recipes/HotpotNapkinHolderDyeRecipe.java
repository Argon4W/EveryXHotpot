package com.github.argon4w.hotpot.recipes;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.items.HotpotSpicePackItem;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class HotpotNapkinHolderDyeRecipe extends CustomRecipe {
    public HotpotNapkinHolderDyeRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        return new SimpleRecipeMatcher(input).with(this::isDyeItem).atLeast(1).with(this::matchSpicePackItem).once().withRemaining().empty().match();
    }

    private boolean matchSpicePackItem(ItemStack itemStack) {
        return itemStack.is(HotpotModEntry.HOTPOT_NAPKIN_HOLDER);
    }

    private boolean isDyeItem(ItemStack itemStack) {
        return itemStack.getItem() instanceof DyeItem;
    }

    @NotNull
    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registryAccess) {
        return new SimpleRecipeAssembler(input).with(itemStack -> itemStack.is(HotpotModEntry.HOTPOT_NAPKIN_HOLDER)).feed(this::assembleSpicePack).assemble();
    }

    private ItemStack assembleSpicePack(ItemStack assembled, ItemStack ingredient) {
        if (ingredient.isEmpty()) {
            return assembled;
        }

        if (!(ingredient.getItem() instanceof DyeItem dyeItem)) {
            return assembled;
        }

        return DyedItemColor.applyDyes(assembled, List.of(dyeItem));
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * width >= 2;
    }

    @NotNull
    @Override
    public RecipeSerializer<?> getSerializer() {
        return HotpotModEntry.HOTPOT_SPICE_PACK_SPECIAL_RECIPE.get();
    }
}
