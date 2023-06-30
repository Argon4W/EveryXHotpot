package com.github.argon4w.hotpot;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record HotpotBlockEntitySyncItemMessage(BlockPos pos) {
    public static void encode(HotpotBlockEntitySyncItemMessage message, FriendlyByteBuf byteBuf) {
        byteBuf.writeLong(message.pos().asLong());
    }

    public static HotpotBlockEntitySyncItemMessage decode(FriendlyByteBuf byteBuf) {
        return new HotpotBlockEntitySyncItemMessage(BlockPos.of(byteBuf.readLong()));
    }

    public static void handle(HotpotBlockEntitySyncItemMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(() -> {
            ServerPlayer sender = contextSupplier.get().getSender();
            if (sender != null && sender.serverLevel().getBlockEntity(message.pos()) instanceof HotpotBlockEntity hotpotBlockEntity) {
                hotpotBlockEntity.markShouldSendItemUpdate();
            }
        });
        contextSupplier.get().setPacketHandled(true);
    }
}
