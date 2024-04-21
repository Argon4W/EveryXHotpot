package com.github.argon4w.hotpot.items;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.blocks.HotpotPlacementBlockEntity;
import com.github.argon4w.hotpot.placements.IHotpotPlacement;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.SoundType;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class HotpotPlacementBlockItem extends BlockItem {
    private final Supplier<IHotpotPlacement> supplier;

    public HotpotPlacementBlockItem(Supplier<IHotpotPlacement> supplier) {
        super(HotpotModEntry.HOTPOT_PLACEMENT.get(), new Properties().stacksTo(64));

        this.supplier = supplier;
    }

    public HotpotPlacementBlockItem(Supplier<IHotpotPlacement> supplier, Properties properties) {
        super(HotpotModEntry.HOTPOT_PLACEMENT.get(), properties);

        this.supplier = supplier;
    }

    public boolean canPlace(Player player, InteractionHand hand, LevelBlockPos pos) {
        return true;
    }

    public void fillPlacementData(HotpotPlacementBlockEntity hotpotPlacementBlockEntity, LevelBlockPos pos, IHotpotPlacement placeable, ItemStack itemStack) {

    }

    @NotNull
    @Override
    public InteractionResult useOn(UseOnContext context) {
        LevelBlockPos selfPos = LevelBlockPos.fromUseOnContext(context);
        Direction direction = context.getHorizontalDirection();
        int pos = HotpotPlacementBlockEntity.getHitPos(context);
        IHotpotPlacement placeable = supplier.get();
        Player player = context.getPlayer();

        if (!canPlace(context.getPlayer(), context.getHand(), selfPos)) {
            return InteractionResult.PASS;
        }

        if (!selfPos.is(HotpotModEntry.HOTPOT_PLACEMENT.get())) {
            return super.useOn(context);
        }

        if (!placeable.canPlace(pos, direction)) {
            return super.useOn(context);
        }

        if (place(selfPos, placeable, context.getItemInHand().copy(), pos)) {
            playSound(selfPos, context.getPlayer());

            if (player == null || !player.getAbilities().instabuild) {
                context.getItemInHand().shrink(1);
            }

            return InteractionResult.FAIL;
        }

        return super.useOn(context);
    }

    @Override
    public String getDescriptionId() {
        return this.getOrCreateDescriptionId();
    }

    @NotNull
    @Override
    public InteractionResult place(BlockPlaceContext context) {
        LevelBlockPos selfPos = LevelBlockPos.fromBlockPlaceContext(context);
        Direction direction = context.getHorizontalDirection();
        int pos = HotpotPlacementBlockEntity.getHitPos(context);
        ItemStack itemStack = context.getItemInHand().copy();
        IHotpotPlacement placeable = supplier.get();

        if (placeable.canPlace(pos, direction)) {
            InteractionResult result = super.place(context);
            place(selfPos, placeable, itemStack, pos);

            return result;
        }

        return InteractionResult.FAIL;
    }

    public boolean place(LevelBlockPos selfPos, IHotpotPlacement placeable, ItemStack itemStack, int pos) {
        if (!selfPos.isServerSide()) {
            return false;
        }

        if (!(selfPos.getBlockEntity() instanceof HotpotPlacementBlockEntity hotpotPlacementBlockEntity)) {
            return false;
        }

        fillPlacementData(hotpotPlacementBlockEntity, selfPos, placeable, itemStack);
        return hotpotPlacementBlockEntity.place(placeable, pos);
    }

    public void playSound(LevelBlockPos pos, Player player) {
        SoundType soundtype = pos.getSoundType(player);
        SoundEvent soundEvent = this.getPlaceSound(pos.getBlockState(), pos.level(), pos.pos(), player);
        float volume = (soundtype.getVolume() + 1.0F) / 2.0F;
        float pitch = soundtype.getPitch() * 0.8F;

        pos.level().playSound(player, pos.pos(), soundEvent, SoundSource.BLOCKS, volume, pitch);
    }
}
