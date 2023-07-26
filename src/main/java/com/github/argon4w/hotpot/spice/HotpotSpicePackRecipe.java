package com.github.argon4w.hotpot.spice;

import com.github.argon4w.hotpot.HotpotModEntry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class HotpotSpicePackRecipe extends CustomRecipe {
    public static final Predicate<ItemStack> PREDICATE = itemStack -> itemStack.is(HotpotModEntry.HOTPOT_SPICE_PACK.get());
    public static final Supplier<ItemStack> SUPPLIER = () -> new ItemStack(HotpotModEntry.HOTPOT_SPICE_PACK.get());

    public HotpotSpicePackRecipe(ResourceLocation p_252125_, CraftingBookCategory p_249010_) {
        super(p_252125_, p_249010_);
    }

    @Override
    public boolean matches(CraftingContainer craftingContainer, Level level) {
        return new CraftingMatcher(craftingContainer)
                .atLeast(1, itemStack -> itemStack.is(ItemTags.SMALL_FLOWERS))
                .require(1, HotpotSpicePackRecipe.PREDICATE).match();
    }

    @NotNull
    @Override
    public ItemStack assemble(CraftingContainer craftingContainer, RegistryAccess registryAccess) {
        return new CraftingAssembler(craftingContainer)
                .withExisting(HotpotSpicePackRecipe.PREDICATE, HotpotSpicePackRecipe.SUPPLIER)
                /*.filter(itemStack -> !HotpotSpicePackRecipe.PREDICATE.test(itemStack))*/
                .forEach((assembled, itemStack) -> {
                    ListTag list = assembled.getOrCreateTag().getList("Spices", Tag.TAG_COMPOUND);
                    list.add(itemStack.copyWithCount(1).save(new CompoundTag()));

                    assembled.getTag().put("Spices", list);
                    assembled.getTag().putInt("Amount", 20);
                })
                .assemble();
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
