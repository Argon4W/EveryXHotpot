package com.github.argon4w.hotpot.mixins;

import com.github.argon4w.hotpot.HotpotModEntry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Inject(method = "isAlliedTo(Lnet/minecraft/world/entity/Entity;)Z", at = @At("RETURN"), cancellable = true)
    public void isAlliedTo(Entity pEntity, CallbackInfoReturnable<Boolean> cir) {
        if (!(this instanceof Enemy)) {
            return;
        }

        if (!(pEntity instanceof LivingEntity livingEntity)) {
            return;
        }

        if (!livingEntity.hasEffect(HotpotModEntry.HOTPOT_SMELLY)) {
            return;
        }

        cir.setReturnValue(true);
    }
}
