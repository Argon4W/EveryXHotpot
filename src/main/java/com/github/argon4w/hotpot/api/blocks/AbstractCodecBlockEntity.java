package com.github.argon4w.hotpot.api.blocks;

import com.github.argon4w.hotpot.LevelBlockPos;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractCodecBlockEntity<T, P extends AbstractCodecBlockEntity.PartialData<T>>
        extends BlockEntity {
    protected T data;

    public AbstractCodecBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

    public abstract T getDefaultData(HolderLookup.Provider registryAccess);

    public abstract Codec<T> getFullCodec();

    public abstract Codec<P> getPartialCodec();

    public abstract P getPartialData(HolderLookup.Provider registryAccess);

    public abstract void onPartialDataUpdated();

    public abstract BlockEntity getBlockEntity();

    public abstract T onFullDataUpdate(T data);

    public abstract T onFullDataUpdate(LevelBlockPos pos, T data);

    public Codec<Either<T, P>> getCodec() {
        return Codec.either(getFullCodec(), getPartialCodec());
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(getBlockEntity(), (blockEntity, registryAccess) -> {
            CompoundTag compoundTag = new CompoundTag();

            compoundTag.put(
                    "value",
                    getCodec()
                            .encodeStart(
                                    RegistryOps.create(NbtOps.INSTANCE, registryAccess),
                                    Either.right(getPartialData(registryAccess)))
                            .resultOrPartial()
                            .orElse(new CompoundTag()));
            onPartialDataUpdated();

            return compoundTag;
        });
    }

    @NotNull @Override
    public CompoundTag getUpdateTag(HolderLookup.@NotNull Provider registryAccess) {
        CompoundTag compoundTag = new CompoundTag();

        compoundTag.put(
                "value",
                getCodec()
                        .encodeStart(RegistryOps.create(NbtOps.INSTANCE, registryAccess), Either.left(data))
                        .resultOrPartial()
                        .orElse(new CompoundTag()));

        return compoundTag;
    }

    @Override
    protected void loadAdditional(CompoundTag compoundTag, HolderLookup.@NotNull Provider registryAccess) {
        data = getCodec()
                .parse(RegistryOps.create(NbtOps.INSTANCE, registryAccess), compoundTag.getCompound("value"))
                .resultOrPartial()
                .map(either -> either.map(
                        data -> hasLevel()
                                ? onFullDataUpdate(new LevelBlockPos(getLevel(), getBlockPos()), data)
                                : onFullDataUpdate(data),
                        partial -> partial.update(data)))
                .orElse(getDefaultData(registryAccess));
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag, HolderLookup.@NotNull Provider registryAccess) {
        compoundTag.put(
                "value",
                getCodec()
                        .encodeStart(RegistryOps.create(NbtOps.INSTANCE, registryAccess), Either.left(data))
                        .resultOrPartial()
                        .orElse(new CompoundTag()));
    }

    @Override
    public void setLevel(@NotNull Level pLevel) {
        super.setLevel(pLevel);

        if (data == null) {
            data = getDefaultData(pLevel.registryAccess());
        }
    }

    public interface PartialData<T> {
        T update(T data);
    }
}
