package com.github.argon4w.hotpot.mixins.effects;

import com.github.argon4w.hotpot.HotpotModEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelReader;
import net.neoforged.neoforge.common.extensions.IBlockStateExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(IBlockStateExtension.class)
public interface IBlockStateExtensionMixin {
    @Inject(method = "getFriction", at = @At("RETURN"), cancellable = true)
    default void getFriction(LevelReader level, BlockPos pos, Entity entity, CallbackInfoReturnable<Float> cir) {
        if (!(entity instanceof LivingEntity livingEntity)) {
            return;
        }

        if (!livingEntity.hasEffect(HotpotModEntry.HOTPOT_GREASY)) {
            return;
        }

        cir.setReturnValue(0.9999f);
    }
}
