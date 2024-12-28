package com.github.argon4w.hotpot.api.items;

import com.github.argon4w.fancytoys.blocks.LevelBlockPos;
import com.github.argon4w.hotpot.api.blocks.IHotpotTablewareContainer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface IHotpotTablewareInteraction {

    void interact(Context context, ItemStack itemStack, IHotpotTablewareContainer blockEntity);

    record Context(int position, int layer, Player player, InteractionHand hand, LevelBlockPos pos) {

        public Context position(int position) {
            return new Context(position, layer, player, hand, pos);
        }

        public Context layer(int layer) {
            return new Context(position, layer, player, hand, pos);
        }

        public Context player(Player player) {
            return new Context(position, layer, player, hand, pos);
        }

        public Context hand(InteractionHand hand) {
            return new Context(position, layer, player, hand, pos);
        }

        public Context pos(LevelBlockPos pos) {
            return new Context(position, layer, player, hand, pos);
        }
    }
}
