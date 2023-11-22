package com.github.argon4w.hotpot.items;

import com.github.argon4w.hotpot.BlockPosWithLevel;
import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.blocks.HotpotPlaceableBlock;
import com.github.argon4w.hotpot.blocks.HotpotPlaceableBlockEntity;
import com.github.argon4w.hotpot.placeables.IHotpotPlaceable;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.SoundType;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class HotpotPlaceableBlockItem extends BlockItem {
    private final Supplier<IHotpotPlaceable> supplier;

    public HotpotPlaceableBlockItem(Supplier<IHotpotPlaceable> supplier) {
        super(HotpotModEntry.HOTPOT_PLACEABLE.get(), new Properties().stacksTo(64).tab(HotpotModEntry.HOTPOT_CREATIVE_TAB));

        this.supplier = supplier;
    }

    public HotpotPlaceableBlockItem(Supplier<IHotpotPlaceable> supplier, Properties properties) {
        super(HotpotModEntry.HOTPOT_PLACEABLE.get(), properties);

        this.supplier = supplier;
    }

    @Override
    public void fillItemCategory(CreativeModeTab p_41391_, NonNullList<ItemStack> p_41392_) {
        if (this.allowedIn(p_41391_)) {
            p_41392_.add(new ItemStack(this));
        }

    }

    public boolean shouldPlace(Player player, InteractionHand hand, BlockPosWithLevel pos) {
        return true;
    }

    public void setAdditional(HotpotPlaceableBlockEntity hotpotPlaceableBlockEntity, BlockPosWithLevel pos, IHotpotPlaceable placeable, ItemStack itemStack) {

    }

    @NotNull
    @Override
    public InteractionResult useOn(UseOnContext context) {
        BlockPosWithLevel selfPos = BlockPosWithLevel.fromUseOnContext(context);
        Direction direction = context.getHorizontalDirection();
        int pos = HotpotPlaceableBlockEntity.getHitPos(context);

        if (!shouldPlace(context.getPlayer(), context.getHand(), selfPos)) {
            return InteractionResult.PASS;
        }

        IHotpotPlaceable placeable = supplier.get();
        Player player = context.getPlayer();

        if (selfPos.is(HotpotModEntry.HOTPOT_PLACEABLE.get()) && placeable.tryPlace(pos, direction) && tryPlace(selfPos, placeable, context.getItemInHand().copy())) {
            playSound(selfPos, context.getPlayer());

            if (player == null || !player.getAbilities().instabuild) {
                context.getItemInHand().shrink(1);

                return InteractionResult.sidedSuccess(!selfPos.isServerSide());
            }

            return InteractionResult.FAIL;
        }

        return super.useOn(context);
    }

    @NotNull
    @Override
    public InteractionResult place(BlockPlaceContext context) {
        BlockPosWithLevel selfPos = BlockPosWithLevel.fromBlockPlaceContext(context);
        Direction direction = context.getHorizontalDirection();
        int pos = HotpotPlaceableBlockEntity.getHitPos(context);
        ItemStack itemStack = context.getItemInHand().copy();

        IHotpotPlaceable placeable = supplier.get();

        if (placeable.tryPlace(pos, direction)) {
            InteractionResult result = super.place(context);
            tryPlace(selfPos, placeable, itemStack);

            return result;
        }

        return InteractionResult.FAIL;
    }

    public boolean tryPlace(BlockPosWithLevel selfPos, IHotpotPlaceable placeable, ItemStack itemStack) {
        if (selfPos.isServerSide() && selfPos.getBlockEntity() instanceof HotpotPlaceableBlockEntity hotpotPlateBlockEntity) {
            setAdditional(hotpotPlateBlockEntity, selfPos, placeable, itemStack);
            return hotpotPlateBlockEntity.tryPlace(placeable);
        }

        return false;
    }

    public void playSound(BlockPosWithLevel pos, Player player) {
        SoundType soundtype = pos.getSoundType(player);
        SoundEvent soundEvent = this.getPlaceSound(pos.getBlockState(), pos.level(), pos.pos(), player);
        float volume = (soundtype.getVolume() + 1.0F) / 2.0F;
        float pitch = soundtype.getPitch() * 0.8F;

        pos.level().playSound(player, pos.pos(), soundEvent, SoundSource.BLOCKS, volume, pitch);
    }

    @NotNull
    @Override
    public String getDescriptionId() {
        return super.getOrCreateDescriptionId();
    }
}
