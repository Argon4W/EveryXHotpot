package com.github.argon4w.hotpot.soups;

import com.github.argon4w.hotpot.BlockPosWithLevel;
import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.contents.HotpotBlastFurnaceRecipeContent;
import com.github.argon4w.hotpot.contents.IHotpotContent;
import com.github.argon4w.hotpot.soups.renderers.IHotpotSoupCustomElementRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

public class HotpotLavaSoup extends AbstractHotpotFluidBasedSoup {
    public HotpotLavaSoup() {
        super(Map.of((itemStack) -> itemStack.is(Items.LAVA_BUCKET), new HotpotFluidRefill(1f, SoundEvents.BUCKET_EMPTY_LAVA, () -> new ItemStack(Items.BUCKET))));
    }

    @Override
    public String getID() {
        return "LavaSoup";
    }

    @Override
    public Optional<IHotpotContent> remapItemStack(boolean copy, ItemStack itemStack, BlockPosWithLevel pos) {
        return HotpotBlastFurnaceRecipeContent.hasBlastingRecipe(itemStack, pos) ? Optional.of(new HotpotBlastFurnaceRecipeContent((copy ? itemStack.copy() : itemStack))) : Optional.empty();
    }

    @Override
    public void animateTick(HotpotBlockEntity hotpotBlockEntity, BlockPosWithLevel pos, Random randomSource) {

    }

    @Override
    public float getWaterLevelDropRate() {
        return 0.05f;
    }

    @Override
    public Optional<ResourceLocation> getBubbleResourceLocation() {
        return Optional.of(new ResourceLocation(HotpotModEntry.MODID, "soup/hotpot_lava_soup_bubble"));
    }

    @Override
    public Optional<ResourceLocation> getSoupResourceLocation() {
        return Optional.of(new ResourceLocation(HotpotModEntry.MODID, "soup/hotpot_lava_soup"));
    }

    @Override
    public List<IHotpotSoupCustomElementRenderer> getCustomElementRenderers() {
        return List.of();
    }
}
