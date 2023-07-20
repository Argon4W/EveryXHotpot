package com.github.argon4w.hotpot.items;

import com.github.argon4w.hotpot.BlockPosWithLevel;
import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.blocks.HotpotPlaceableBlockEntity;
import com.github.argon4w.hotpot.placeables.IHotpotPlaceable;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.SoundType;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class HotpotPlateBlockItem extends BlockItem {
    private final Supplier<IHotpotPlaceable> supplier;

    public HotpotPlateBlockItem(Supplier<IHotpotPlaceable> supplier) {
        super(HotpotModEntry.HOTPOT_PLATE.get(), new Properties().stacksTo(64));

        this.supplier = supplier;
    }

    @NotNull
    @Override
    public InteractionResult useOn(UseOnContext context) {
        BlockPosWithLevel pos = BlockPosWithLevel.fromUseOnContext(context);
        Direction direction = context.getHorizontalDirection();
        int hitSlot = HotpotPlaceableBlockEntity.getHitPos(context);

        IHotpotPlaceable plate = supplier.get();
        Player player = context.getPlayer();

        if (pos.is(HotpotModEntry.HOTPOT_PLATE.get()) && plate.tryPlace(hitSlot, direction) && tryPlace(pos, plate)) {
            playSound(pos, context.getPlayer());

            if (player == null || !player.getAbilities().instabuild) {
                context.getItemInHand().shrink(1);

                return InteractionResult.sidedSuccess(!pos.isServerSide());
            }

            return InteractionResult.FAIL;
        }

        return super.useOn(context);
    }

    @NotNull
    @Override
    public InteractionResult place(BlockPlaceContext context) {
        BlockPosWithLevel pos = BlockPosWithLevel.fromBlockPlaceContext(context);
        Direction direction = context.getHorizontalDirection();
        int hitSlot = HotpotPlaceableBlockEntity.getHitPos(context);

        IHotpotPlaceable plate = supplier.get();

        if (plate.tryPlace(hitSlot, direction)) {
            InteractionResult result = super.place(context);
            tryPlace(pos, plate);

            return result;
        }

        return InteractionResult.FAIL;
    }

    public boolean tryPlace(BlockPosWithLevel pos, IHotpotPlaceable plate) {
        if (pos.isServerSide() && pos.getBlockEntity() instanceof HotpotPlaceableBlockEntity hotpotPlateBlockEntity) {
            return hotpotPlateBlockEntity.tryPlace(plate);
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
