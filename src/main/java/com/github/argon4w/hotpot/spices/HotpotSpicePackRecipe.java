package com.github.argon4w.hotpot.spices;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.HotpotTagsHelper;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.ArrayList;
import java.util.List;

public class HotpotSpicePackRecipe extends SpecialRecipe {
    public HotpotSpicePackRecipe(ResourceLocation p_252125_) {
        super(p_252125_);
    }

    @Override
    public boolean matches(CraftingInventory craftingContainer, World level) {
        List<ItemStack> list = new ArrayList<>();

        return new HotpotSpiceMatcher(craftingContainer)
                .with(itemStack -> itemStack.getItem().is(ItemTags.SMALL_FLOWERS)).collect(list::add).atLeast(1)
                .with(itemStack -> itemStack.getItem().equals(HotpotModEntry.HOTPOT_SPICE_PACK.get()) && ((HotpotTagsHelper.hasHotpotTag(itemStack) ? HotpotTagsHelper.getHotpotTag(itemStack).getList("Spices", Constants.NBT.TAG_COMPOUND).size() : 0) + list.size() <= 4)).once()
                .withRemaining().empty()
                .match();
    }

    @NotNull
    @Override
    public ItemStack assemble(CraftingInventory craftingContainer) {
        return new HotpotSpiceAssembler(craftingContainer)
                .withExisting(
                        itemStack -> itemStack.getItem().equals(HotpotModEntry.HOTPOT_SPICE_PACK.get()),
                        () -> new ItemStack(HotpotModEntry.HOTPOT_SPICE_PACK.get())
                )
                /*.filter(itemStack -> !HotpotSpicePackRecipe.PREDICATE.test(itemStack))*/
                .forEach((assembled, itemStack) -> {
                    ListNBT list = HotpotTagsHelper.getHotpotTag(assembled).getList("Spices", Constants.NBT.TAG_COMPOUND);

                    ItemStack copied = itemStack.copy();
                    copied.setCount(1);

                    list.add(copied.save(new CompoundNBT()));

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
    public IRecipeSerializer<?> getSerializer() {
        return HotpotModEntry.HOTPOT_SPICE_PACK_SPECIAL_RECIPE.get();
    }
}
