package com.github.argon4w.hotpot.spices;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.HotpotTagsHelper;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class HotpotSpicePackRecipe extends CustomRecipe {
    public HotpotSpicePackRecipe(ResourceLocation p_252125_) {
        super(p_252125_);
    }

    @Override
    public boolean matches(CraftingContainer craftingContainer, Level level) {
        List<ItemStack> list = new ArrayList<>();

        return new HotpotSpiceMatcher(craftingContainer)
                .with(itemStack -> itemStack.is(ItemTags.SMALL_FLOWERS)).collect(list::add).atLeast(1)
                .with(itemStack -> itemStack.is(HotpotModEntry.HOTPOT_SPICE_PACK.get()) && ((HotpotTagsHelper.hasHotpotTag(itemStack) ? HotpotTagsHelper.getHotpotTag(itemStack).getList("Spices", Tag.TAG_COMPOUND).size() : 0) + list.size() <= 4)).once()
                .withRemaining().empty()
                .match();
    }

    @NotNull
    @Override
    public ItemStack assemble(CraftingContainer craftingContainer) {
        return new HotpotSpiceAssembler(craftingContainer)
                .withExisting(
                        itemStack -> itemStack.is(HotpotModEntry.HOTPOT_SPICE_PACK.get()),
                        () -> new ItemStack(HotpotModEntry.HOTPOT_SPICE_PACK.get())
                )
                /*.filter(itemStack -> !HotpotSpicePackRecipe.PREDICATE.test(itemStack))*/
                .forEach((assembled, itemStack) -> {
                    ListTag list = HotpotTagsHelper.getHotpotTag(assembled).getList("Spices", Tag.TAG_COMPOUND);
                    ItemStack copied = itemStack.copy();
                    copied.setCount(1);
                    list.add(copied.save(new CompoundTag()));

                    HotpotTagsHelper.updateHotpotTag(assembled, compoundTag -> compoundTag.put("Spices", list));
                    HotpotTagsHelper.updateHotpotTag(assembled, compoundTag -> compoundTag.putInt("SpiceAmount", 20));
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
