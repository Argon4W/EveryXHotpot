package com.github.argon4w.hotpot.blocks;

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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Function;

public abstract class AbstractHotpotCodecTablewareBlockEntity<T, P extends AbstractHotpotCodecTablewareBlockEntity.PartialData<T>> extends AbstractHotpotTablewareBlockEntity {
    public AbstractHotpotCodecTablewareBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

    public abstract T getDefaultData();
    public abstract Codec<T> getFullCodec();
    public abstract Codec<P> getPartialCodec();
    public abstract P getPartialData(HolderLookup.Provider registryAccess);
    public abstract T getData();
    public abstract void setData(T data);
    public abstract BlockEntity getBlockEntity();

    public Codec<Either<T, P>> getCodec() {
        return Codec.either(getFullCodec(), getPartialCodec());
    }

    @Override
    protected void loadAdditional(CompoundTag compoundTag, HolderLookup.Provider registryAccess) {
        setData(getCodec().parse(RegistryOps.create(NbtOps.INSTANCE, registryAccess), compoundTag.getCompound("value")).resultOrPartial().map(either -> either.map(Function.identity(), partial -> partial.update(getData()))).orElse(getDefaultData()));
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag, HolderLookup.Provider registryAccess) {
        compoundTag.put("value", getCodec().encodeStart(RegistryOps.create(NbtOps.INSTANCE, registryAccess), Either.left(getData())).resultOrPartial().orElse(new CompoundTag()));
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registryAccess) {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.put("value", getCodec().encodeStart(RegistryOps.create(NbtOps.INSTANCE, registryAccess), Either.left(getData())).resultOrPartial().orElse(new CompoundTag()));
        return compoundTag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(getBlockEntity(), (blockEntity, registryAccess) -> {
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.put("value", getCodec().encodeStart(RegistryOps.create(NbtOps.INSTANCE, registryAccess), Either.right(getPartialData(registryAccess))).resultOrPartial().orElse(new CompoundTag()));
            return compoundTag;
        });
    }

    interface PartialData<T> {
        T update(T data);
    }
}
