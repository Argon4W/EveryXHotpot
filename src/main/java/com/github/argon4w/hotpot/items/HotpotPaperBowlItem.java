package com.github.argon4w.hotpot.items;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotPlacementBlockEntity;
import com.github.argon4w.hotpot.client.items.IHotpotSpecialRenderedItem;
import com.github.argon4w.hotpot.placements.HotpotPlacedChopstick;
import com.github.argon4w.hotpot.placements.HotpotPlacedPaperBowl;
import com.github.argon4w.hotpot.placements.HotpotPlacements;
import com.github.argon4w.hotpot.placements.IHotpotPlacement;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

public class HotpotPaperBowlItem extends HotpotPlacementBlockItem implements IHotpotSpecialRenderedItem {
    public HotpotPaperBowlItem() {
        super(() -> HotpotPlacements.PLACED_PAPER_BOWL.get().build());
    }

    @Override
    public void fillPlacementData(HotpotPlacementBlockEntity hotpotPlacementBlockEntity, LevelBlockPos pos, IHotpotPlacement placement, ItemStack itemStack) {
        if (placement instanceof HotpotPlacedPaperBowl placedPaperBowl) {
            placedPaperBowl.setPaperBowlItemStack(itemStack);
        }
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return HotpotModEntry.HOTPOT_SPECIAL_ITEM_RENDERER;
            }
        });
    }

    @Override
    public ResourceLocation getSpecialRendererResourceLocation() {
        return new ResourceLocation(HotpotModEntry.MODID, "paper_bowl_renderer");
    }
}
