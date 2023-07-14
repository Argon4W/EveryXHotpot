package com.github.argon4w.hotpot.events;

import com.github.argon4w.hotpot.BlockPosWithLevel;
import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.contents.HotpotPlayerContent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = HotpotModEntry.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class HotpotGameModEvents {
    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.getSource().is(HotpotModEntry.IN_HOTPOT_DAMAGE_KEY) && event.getEntity() instanceof Player player) {
            Vec3 vec = event.getSource().getSourcePosition();

            if (vec != null) {
                BlockPosWithLevel pos = BlockPosWithLevel.fromVec3(event.getEntity().level(), vec);

                if (pos.getBlockEntity() instanceof HotpotBlockEntity hotpotBlockEntity) {
                    hotpotBlockEntity.tryPlaceContent(0, new HotpotPlayerContent(player, true), pos);
                    hotpotBlockEntity.tryPlaceContent(0, new HotpotPlayerContent(player, false), pos);
                    hotpotBlockEntity.tryPlaceContent(0, new HotpotPlayerContent(player, false), pos);
                }
            }
        }
    }
}
