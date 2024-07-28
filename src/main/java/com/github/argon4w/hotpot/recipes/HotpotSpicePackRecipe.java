package com.github.argon4w.hotpot.recipes;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.items.HotpotSpicePackItem;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class HotpotSpicePackRecipe extends CustomRecipe {
    public HotpotSpicePackRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        List<ItemStack> list = new ArrayList<>();
        return new SimpleRecipeMatcher(input).with(this::hasSuspiciousEffects).collect(list::add).atLeast(1).with(itemStack -> matchSpicePackItem(itemStack, list.size())).once().withRemaining().empty().match();
    }

    private boolean matchSpicePackItem(ItemStack itemStack, int count) {
        return itemStack.is(HotpotModEntry.HOTPOT_SPICE_PACK) && HotpotSpicePackItem.getSpicePackItems(itemStack).size() + count <= 4;
    }

    private boolean hasSuspiciousEffects(ItemStack itemStack) {
        return itemStack.is(ItemTags.SMALL_FLOWERS);
    }

    @NotNull
    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registryAccess) {
        return new SimpleRecipeAssembler(input).with(itemStack -> itemStack.is(HotpotModEntry.HOTPOT_SPICE_PACK.get())).feed(this::assembleSpicePack).assemble();
    }

    private void assembleSpicePack(ItemStack assembled, ItemStack ingredient) {
        HotpotSpicePackItem.addSpicePackItems(assembled, ingredient.copyWithCount(1));
        HotpotSpicePackItem.setSpicePackCharges(assembled, 20);
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
