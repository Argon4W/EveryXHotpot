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

public class HotpotPlaceableBlockItem extends BlockItem {
    private final Supplier<IHotpotPlaceable> supplier;

    public HotpotPlaceableBlockItem(Supplier<IHotpotPlaceable> supplier) {
        super(HotpotModEntry.HOTPOT_PLACEABLE.get(), new Properties().stacksTo(64));

        this.supplier = supplier;
    }

    @NotNull
    @Override
    public InteractionResult useOn(UseOnContext context) {
        BlockPosWithLevel selfPos = BlockPosWithLevel.fromUseOnContext(context);
        Direction direction = context.getHorizontalDirection();
        int pos = HotpotPlaceableBlockEntity.getHitPos(context);

        IHotpotPlaceable placeable = supplier.get();
        Player player = context.getPlayer();

        if (selfPos.is(HotpotModEntry.HOTPOT_PLACEABLE.get()) && placeable.tryPlace(pos, direction) && tryPlace(selfPos, placeable)) {
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

        IHotpotPlaceable placeable = supplier.get();

        if (placeable.tryPlace(pos, direction)) {
            InteractionResult result = super.place(context);
            tryPlace(selfPos, placeable);

            return result;
        }

        return InteractionResult.FAIL;
    }

    public boolean tryPlace(BlockPosWithLevel selfPos, IHotpotPlaceable plate) {
        if (selfPos.isServerSide() && selfPos.getBlockEntity() instanceof HotpotPlaceableBlockEntity hotpotPlateBlockEntity) {
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
