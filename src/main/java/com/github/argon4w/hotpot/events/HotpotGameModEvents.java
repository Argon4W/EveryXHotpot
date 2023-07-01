package com.github.argon4w.hotpot.events;

import com.github.argon4w.hotpot.HotpotModEntry;
import com.github.argon4w.hotpot.blocks.HotpotBlockEntity;
import com.github.argon4w.hotpot.contents.HotpotPlayerContent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
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
                BlockPos pos = new BlockPos((int) vec.x, (int) vec.y, (int) vec.z);
                Level level = event.getEntity().level();

                if (level.getBlockEntity(pos) instanceof HotpotBlockEntity hotpotBlockEntity) {
                    hotpotBlockEntity.placeContent(0, new HotpotPlayerContent(player, true), level, pos);
                    hotpotBlockEntity.placeContent(0, new HotpotPlayerContent(player, false), level, pos);
                    hotpotBlockEntity.placeContent(0, new HotpotPlayerContent(player, false), level, pos);
                }
            }
        }
    }
}
