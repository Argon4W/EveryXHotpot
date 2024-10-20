package com.github.argon4w.hotpot.recipes;

import com.github.argon4w.hotpot.HotpotModEntry;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

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
        return new SimpleRecipeAssembler(input).with(itemStack -> itemStack.is(HotpotModEntry.HOTPOT_NAPKIN_HOLDER)).filter(itemStack -> !itemStack.isEmpty()).filter(itemStack -> itemStack.getItem() instanceof DyeItem).feed(this::assembleDyedNapkinHolder).assemble();
    }

    private ItemStack assembleDyedNapkinHolder(ItemStack assembled, ItemStack ingredient) {
        return DyedItemColor.applyDyes(assembled, List.of((DyeItem) ingredient.getItem()));
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * width >= 2;
    }

    @NotNull
    @Override
    public RecipeSerializer<?> getSerializer() {
        return HotpotModEntry.HOTPOT_NAPKIN_HOLDER_DYE_SPECIAL_RECIPE.get();
    }
}
