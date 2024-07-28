package com.github.argon4w.hotpot.items;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.LevelBlockPos;
import com.github.argon4w.hotpot.blocks.HotpotPlacementBlockEntity;
import com.github.argon4w.hotpot.placements.IHotpotPlacement;
import com.github.argon4w.hotpot.placements.IHotpotPlacementFactory;
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
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;

public class HotpotPlacementBlockItem<T extends IHotpotPlacement> extends BlockItem {
    private final DeferredHolder<IHotpotPlacementFactory<?>, ? extends  IHotpotPlacementFactory<T>> holder;

    public HotpotPlacementBlockItem(DeferredHolder<IHotpotPlacementFactory<?>, ? extends IHotpotPlacementFactory<T>> holder) {
        super(HotpotModEntry.HOTPOT_PLACEMENT.get(), new Properties().stacksTo(64));
        this.holder = holder;
    }

    public HotpotPlacementBlockItem(DeferredHolder<IHotpotPlacementFactory<?>, ? extends  IHotpotPlacementFactory<T>> holder, Properties properties) {
        super(HotpotModEntry.HOTPOT_PLACEMENT.get(), properties);
        this.holder = holder;
    }

    public boolean canPlace(Player player, InteractionHand hand, LevelBlockPos pos) {
        return true;
    }

    public void loadPlacement(HotpotPlacementBlockEntity hotpotPlacementBlockEntity, LevelBlockPos pos, T placement, ItemStack itemStack) {

    }

    @NotNull
    @Override
    public InteractionResult useOn(UseOnContext context) {
        LevelBlockPos selfPos = LevelBlockPos.fromUseOnContext(context);
        Direction direction = context.getHorizontalDirection();
        int pos = HotpotPlacementBlockEntity.getHitPos(context);

        IHotpotPlacementFactory<T> factory = holder.value();
        Player player = context.getPlayer();

        if (!selfPos.is(HotpotModEntry.HOTPOT_PLACEMENT.get())) {
            selfPos = LevelBlockPos.fromBlockPlaceContext(new BlockPlaceContext(context));
        }

        if (!canPlace(context.getPlayer(), context.getHand(), selfPos)) {
            return InteractionResult.PASS;
        }

        if (!selfPos.is(HotpotModEntry.HOTPOT_PLACEMENT.get())) {
            return super.useOn(context);
        }

        if (!factory.canPlace(pos, direction)) {
            return super.useOn(context);
        }

        if (!place(selfPos, factory.buildFromSlots(pos, direction), context.getItemInHand().copy(), pos)) {
            return InteractionResult.FAIL;
        }

        playSound(selfPos, context.getPlayer());

        if (player == null || !player.getAbilities().instabuild) {
            context.getItemInHand().shrink(1);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public String getDescriptionId() {
        return this.getOrCreateDescriptionId();
    }

    @NotNull
    @Override
    public InteractionResult place(BlockPlaceContext context) {
        LevelBlockPos selfPos = LevelBlockPos.fromBlockPlaceContext(context);
        ItemStack itemStack = context.getItemInHand().copy();
        Direction direction = context.getHorizontalDirection();

        int pos = HotpotPlacementBlockEntity.getHitPos(context);
        IHotpotPlacementFactory<T> factory = holder.value();

        if (!factory.canPlace(pos, direction)) {
            return InteractionResult.FAIL;
        }

        InteractionResult result = super.place(context);
        place(selfPos, factory.buildFromSlots(pos, direction), itemStack, pos);

        return result;
    }

    public boolean place(LevelBlockPos selfPos, T placement, ItemStack itemStack, int pos) {
        if (!selfPos.isServerSide()) {
            return false;
        }

        if (!(selfPos.getBlockEntity() instanceof HotpotPlacementBlockEntity hotpotPlacementBlockEntity)) {
            return false;
        }

        if (!hotpotPlacementBlockEntity.place(placement, pos)) {
            return false;
        }

        loadPlacement(hotpotPlacementBlockEntity, selfPos, placement, itemStack);
        return true;
    }

    public void playSound(LevelBlockPos pos, Player player) {
        SoundType soundtype = pos.getSoundType(player);
        SoundEvent soundEvent = this.getPlaceSound(pos.getBlockState(), pos.level(), pos.pos(), player);
        float volume = (soundtype.getVolume() + 1.0F) / 2.0F;
        float pitch = soundtype.getPitch() * 0.8F;

        pos.level().playSound(player, pos.pos(), soundEvent, SoundSource.BLOCKS, volume, pitch);
    }
}
