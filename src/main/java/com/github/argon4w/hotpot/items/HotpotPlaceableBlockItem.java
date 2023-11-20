package com.github.argon4w.hotpot.items;

import com.github.argon4w.hotpot.BlockPosWithLevel;
import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.blocks.HotpotPlaceableBlockEntity;
import com.github.argon4w.hotpot.placeables.IHotpotPlaceable;
import net.minecraft.block.SoundType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.*;

import java.util.function.Supplier;

public class HotpotPlaceableBlockItem extends BlockItem {
    private final Supplier<IHotpotPlaceable> supplier;

    public HotpotPlaceableBlockItem(Supplier<IHotpotPlaceable> supplier) {
        super(HotpotModEntry.HOTPOT_PLACEABLE.get(), new Properties().stacksTo(64));

        this.supplier = supplier;
    }

    public HotpotPlaceableBlockItem(Supplier<IHotpotPlaceable> supplier, Properties properties) {
        super(HotpotModEntry.HOTPOT_PLACEABLE.get(), properties);

        this.supplier = supplier;
    }

    public boolean shouldPlace(PlayerEntity player, Hand hand, BlockPosWithLevel pos) {
        return true;
    }

    public void setAdditional(HotpotPlaceableBlockEntity hotpotPlaceableBlockEntity, BlockPosWithLevel pos, IHotpotPlaceable placeable, ItemStack itemStack) {

    }

    @Override
    public void fillItemCategory(ItemGroup pGroup, NonNullList<ItemStack> pItems) {
        if (this.allowdedIn(pGroup)) {
            pItems.add(new ItemStack(this));
        }
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        BlockPosWithLevel selfPos = BlockPosWithLevel.fromUseOnContext(context);
        Direction direction = context.getHorizontalDirection();
        int pos = HotpotPlaceableBlockEntity.getHitPos(context);

        if (!shouldPlace(context.getPlayer(), context.getHand(), selfPos)) {
            return ActionResultType.PASS;
        }

        IHotpotPlaceable placeable = supplier.get();
        PlayerEntity player = context.getPlayer();

        if (selfPos.is(HotpotModEntry.HOTPOT_PLACEABLE.get()) && placeable.tryPlace(pos, direction) && tryPlace(selfPos, placeable, context.getItemInHand().copy())) {
            playSound(selfPos, context.getPlayer());

            if (player == null || !player.abilities.instabuild) {
                context.getItemInHand().shrink(1);

                return ActionResultType.sidedSuccess(!selfPos.isServerSide());
            }

            return ActionResultType.FAIL;
        }

        return super.useOn(context);
    }

    @Override
    public ActionResultType place(BlockItemUseContext context) {
        BlockPosWithLevel selfPos = BlockPosWithLevel.fromBlockPlaceContext(context);
        Direction direction = context.getHorizontalDirection();
        int pos = HotpotPlaceableBlockEntity.getHitPos(context);
        ItemStack itemStack = context.getItemInHand().copy();

        IHotpotPlaceable placeable = supplier.get();

        if (placeable.tryPlace(pos, direction)) {
            ActionResultType result = super.place(context);
            tryPlace(selfPos, placeable, itemStack);

            return result;
        }

        return ActionResultType.FAIL;
    }

    public boolean tryPlace(BlockPosWithLevel selfPos, IHotpotPlaceable placeable, ItemStack itemStack) {
        if (selfPos.isServerSide() && selfPos.getBlockEntity() instanceof HotpotPlaceableBlockEntity) {
            HotpotPlaceableBlockEntity hotpotPlaceableBlockEntity = (HotpotPlaceableBlockEntity) selfPos.getBlockEntity();
            setAdditional(hotpotPlaceableBlockEntity, selfPos, placeable, itemStack);
            return hotpotPlaceableBlockEntity.tryPlace(placeable);
        }

        return false;
    }

    public void playSound(BlockPosWithLevel pos, PlayerEntity player) {
        SoundType soundtype = pos.getSoundType(player);
        SoundEvent soundEvent = this.getPlaceSound(pos.getBlockState(), pos.level(), pos.pos(), player);
        float volume = (soundtype.getVolume() + 1.0F) / 2.0F;
        float pitch = soundtype.getPitch() * 0.8F;

        pos.level().playSound(player, pos.pos(), soundEvent, SoundCategory.BLOCKS, volume, pitch);
    }

    @Override
    public String getDescriptionId() {
        return super.getOrCreateDescriptionId();
    }
}
