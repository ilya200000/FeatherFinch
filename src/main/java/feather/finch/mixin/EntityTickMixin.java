package feather.finch.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityTickMixin {
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void featherfinch$smartTick(CallbackInfo ci) {
        Entity entity = (Entity) (Object) this;
        if (!entity.level().isClientSide && entity.level() instanceof ServerLevel serverLevel) {
            // Если рядом нет игроков (64 блока), тикаем в 5 раз реже
            if (serverLevel.getNearestPlayer(entity.getX(), entity.getY(), entity.getZ(), 64.0, false) == null) {
                if (entity.level().getGameTime() % 5 != 0) {
                    ci.cancel(); 
                }
            }
        }
    }
}
