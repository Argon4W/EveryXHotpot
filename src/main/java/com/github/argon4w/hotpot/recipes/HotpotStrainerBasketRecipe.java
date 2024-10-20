package com.github.argon4w.hotpot.recipes;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.items.HotpotStrainerBasketItem;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class HotpotStrainerBasketRecipe extends CustomRecipe {
    public HotpotStrainerBasketRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        List<ItemStack> list = new ArrayList<>();
        return new SimpleRecipeMatcher(input).with(this::isFood).collect(list::add).atLeast(1).with(itemStack -> matchStrainerBasketItem(itemStack, list.size())).once().withRemaining().empty().match();
    }

    private boolean matchStrainerBasketItem(ItemStack itemStack, int count) {
        return itemStack.is(HotpotModEntry.HOTPOT_STRAINER_BASKET) && HotpotStrainerBasketItem.getStrainerBasketItems(itemStack).size() + count <= 8;
    }

    private boolean isFood(ItemStack itemStack) {
        return (itemStack.has(DataComponents.FOOD) && !itemStack.hasCraftingRemainingItem()) || itemStack.is(HotpotModEntry.HOTPOT_SKEWER);
    }

    @NotNull
    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registryAccess) {
        return new SimpleRecipeAssembler(input).with(itemStack -> itemStack.is(HotpotModEntry.HOTPOT_STRAINER_BASKET)).filter(Predicate.not(ItemStack::isEmpty)).feed(this::assembleStrainerBasket).assemble();
    }

    private ItemStack assembleStrainerBasket(ItemStack assembled, ItemStack ingredient) {
        return Util.make(assembled, assembled2 -> HotpotStrainerBasketItem.addStrainerBasketItems(assembled2, ingredient.copyWithCount(1)));
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * width >= 2;
    }

    @NotNull
    @Override
    public RecipeSerializer<?> getSerializer() {
        return HotpotModEntry.HOTPOT_STRAINER_BASKET_SPECIAL_RECIPE.get();
    }
}
